package br.com.kipperdev.clean.interfaceadapters.gateways;

import br.com.kipperdev.clean.interfaceadapters.gateways.persistence.ConfirmedPaymentEntity;
import br.com.kipperdev.clean.interfaceadapters.gateways.persistence.EnrollmentEntity;
import br.com.kipperdev.clean.interfaceadapters.gateways.persistence.PendingPaymentEntity;
import br.com.kipperdev.clean.interfaceadapters.gateways.persistence.WebhookInboxEntity;
import br.com.kipperdev.clean.usecases.EnrollmentRepository;
import br.com.kipperdev.clean.usecases.PendingPaymentRepository;
import br.com.kipperdev.clean.entities.Enrollment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** JPA/Hibernate persistence adapter. ORM mappings and transactions stay at the outer edge. */
public final class JpaEnrollmentRepository implements EnrollmentRepository, PendingPaymentRepository, AutoCloseable {
    private final EntityManagerFactory entityManagerFactory;

    public JpaEnrollmentRepository(String jdbcUrl) {
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:h2:file:")) {
            throw new IllegalArgumentException("Use a file-backed H2 JDBC URL (jdbc:h2:file:...)");
        }
        var properties = new HashMap<String, Object>();
        properties.put("jakarta.persistence.jdbc.url", jdbcUrl);
        properties.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.jdbc.time_zone", "UTC");
        this.entityManagerFactory = Persistence.createEntityManagerFactory("enrollment-pu", properties);
    }

    @Override public void save(Enrollment enrollment) {
        inTransaction(em -> {
            var existing = em.find(EnrollmentEntity.class, enrollment.id());
            if (existing == null) em.persist(new EnrollmentEntity(enrollment));
            else existing.updateDetails(enrollment.student(), enrollment.course());
        });
    }

    @Override public List<Enrollment> findAll() {
        try (var em = entityManagerFactory.createEntityManager()) {
            return em.createQuery("select e from EnrollmentEntity e order by e.createdAt, e.id", EnrollmentEntity.class)
                    .getResultList().stream().map(EnrollmentEntity::toDomain).toList();
        }
    }

    @Override public Optional<Enrollment> findById(String id) {
        try (var em = entityManagerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(EnrollmentEntity.class, id)).map(EnrollmentEntity::toDomain);
        }
    }

    @Override public void deleteById(String id) {
        inTransaction(em -> {
            var enrollment = em.find(EnrollmentEntity.class, id);
            if (enrollment != null) em.remove(enrollment);
        });
    }

    @Override public void save(PendingEnrollment pending) {
        inTransaction(em -> em.merge(new PendingPaymentEntity(pending)));
    }

    @Override public Optional<PendingEnrollment> findByPaymentReference(String reference) {
        return findPending("select p from PendingPaymentEntity p where p.paymentReference = :key", reference);
    }

    @Override public Optional<PendingEnrollment> findByEnrollmentId(String enrollmentId) {
        return findPending("select p from PendingPaymentEntity p where p.enrollmentId = :key", enrollmentId);
    }

    private Optional<PendingEnrollment> findPending(String jpql, String key) {
        try (var em = entityManagerFactory.createEntityManager()) {
            return em.createQuery(jpql, PendingPaymentEntity.class).setParameter("key", key)
                    .getResultList().stream().findFirst().map(PendingPaymentEntity::toDomain);
        }
    }

    @Override public boolean isPaymentConfirmed(String reference) {
        try (var em = entityManagerFactory.createEntityManager()) {
            return em.find(ConfirmedPaymentEntity.class, reference) != null;
        }
    }

    @Override public void remove(String reference) {
        inTransaction(em -> {
            var entity = em.find(PendingPaymentEntity.class, reference);
            if (entity != null) em.remove(entity);
        });
    }

    @Override public void confirm(PendingEnrollment pending, String event, String orderId) {
        if (!pending.paymentReference().equals(orderId))
            throw new IllegalArgumentException("Webhook order does not match pending payment");
        inTransaction(em -> {
            em.merge(new EnrollmentEntity(pending.enrollment()));
            if (em.find(ConfirmedPaymentEntity.class, orderId) == null)
                em.persist(new ConfirmedPaymentEntity(orderId, pending.enrollment().id()));
            var pendingEntity = em.find(PendingPaymentEntity.class, orderId);
            if (pendingEntity != null) em.remove(pendingEntity);
            var inboxEvent = em.find(WebhookInboxEntity.class, WebhookInboxEntity.key(event, orderId));
            if (inboxEvent != null) inboxEvent.setProcessedAt(LocalDateTime.now());
        });
    }

    @Override public void enqueueWebhook(String event, String orderId, String rawPayload) {
        inTransaction(em -> {
            var key = WebhookInboxEntity.key(event, orderId);
            if (em.find(WebhookInboxEntity.class, key) == null)
                em.persist(new WebhookInboxEntity(event, orderId, rawPayload));
        });
    }

    @Override public List<WebhookEvent> pendingWebhookEvents() {
        try (var em = entityManagerFactory.createEntityManager()) {
            return em.createQuery("select w from WebhookInboxEntity w where w.processedAt is null " +
                            "and (w.nextAttemptAt is null or w.nextAttemptAt <= :now) order by w.receivedAt", WebhookInboxEntity.class)
                    .setParameter("now", LocalDateTime.now()).setMaxResults(100).getResultList()
                    .stream().map(WebhookInboxEntity::toDomain).toList();
        }
    }

    @Override public void markWebhookProcessed(String event, String orderId) {
        inTransaction(em -> {
            var inboxEvent = em.find(WebhookInboxEntity.class, WebhookInboxEntity.key(event, orderId));
            if (inboxEvent != null) inboxEvent.setProcessedAt(LocalDateTime.now());
        });
    }

    @Override public void deferWebhook(String event, String orderId) {
        inTransaction(em -> {
            var inboxEvent = em.find(WebhookInboxEntity.class, WebhookInboxEntity.key(event, orderId));
            if (inboxEvent != null) {
                int delaySeconds = (int) Math.min(3600, 60L << Math.min(inboxEvent.getAttemptCount(), 6));
                inboxEvent.setAttemptCount(inboxEvent.getAttemptCount() + 1);
                inboxEvent.setNextAttemptAt(LocalDateTime.now().plusSeconds(delaySeconds));
            }
        });
    }

    private void inTransaction(java.util.function.Consumer<EntityManager> work) {
        try (var em = entityManagerFactory.createEntityManager()) {
            var transaction = em.getTransaction();
            transaction.begin();
            try {
                work.accept(em);
                transaction.commit();
            } catch (RuntimeException e) {
                if (transaction.isActive()) transaction.rollback();
                throw e;
            }
        }
    }

    @Override public void close() { entityManagerFactory.close(); }
}
