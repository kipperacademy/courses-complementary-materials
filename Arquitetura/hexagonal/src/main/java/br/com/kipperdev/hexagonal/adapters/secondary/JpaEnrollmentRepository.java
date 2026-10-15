package br.com.kipperdev.hexagonal.adapters.secondary;

import br.com.kipperdev.hexagonal.adapters.secondary.persistence.EnrollmentEntity;
import br.com.kipperdev.hexagonal.adapters.secondary.persistence.PendingPixPaymentEntity;
import br.com.kipperdev.hexagonal.application.ports.secondary.EnrollmentRepository;
import br.com.kipperdev.hexagonal.domain.Enrollment;
import br.com.kipperdev.hexagonal.domain.PendingPixPayment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/** JPA adapter: provider entities and transactions stay outside the application core. */
public final class JpaEnrollmentRepository implements EnrollmentRepository, AutoCloseable {
    private final EntityManagerFactory entityManagerFactory;

    public JpaEnrollmentRepository(String jdbcUrl) {
        prepareDatabaseDirectory(jdbcUrl);
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("jakarta.persistence.jdbc.url", jdbcUrl);
        properties.put("jakarta.persistence.jdbc.user", "sa");
        properties.put("jakarta.persistence.jdbc.password", "");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "false");
        this.entityManagerFactory = Persistence.createEntityManagerFactory("enrollment-unit", properties);
    }

    private static void prepareDatabaseDirectory(String jdbcUrl) {
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:h2:"))
            throw new IllegalArgumentException("ENROLLMENT_DB_URL must be a jdbc:h2: URL");
        if (!jdbcUrl.startsWith("jdbc:h2:file:")) return;
        try {
            var fileSpec = jdbcUrl.substring("jdbc:h2:file:".length()).split(";", 2)[0];
            var filePath = Path.of(fileSpec).toAbsolutePath();
            if (filePath.getParent() != null) Files.createDirectories(filePath.getParent());
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not create H2 database directory", e);
        }
    }

    @Override public void save(Enrollment enrollment) {
        inTransaction(em -> {
            var entity = em.find(EnrollmentEntity.class, enrollment.id());
            if (entity == null) em.persist(new EnrollmentEntity(enrollment.id(), enrollment.student(), enrollment.course(), enrollment.amountInCents()));
            else entity.update(enrollment.student(), enrollment.course(), enrollment.amountInCents());
            return null;
        });
    }

    @Override public List<Enrollment> findAll() {
        return read(em -> em.createQuery("select e from EnrollmentEntity e order by e.id", EnrollmentEntity.class)
                .getResultList().stream().map(e -> new Enrollment(e.getId(), e.getStudent(), e.getCourse(), e.getAmountInCents())).toList());
    }

    @Override public Optional<Enrollment> findById(String id) {
        return read(em -> Optional.ofNullable(em.find(EnrollmentEntity.class, id))
                .map(e -> new Enrollment(e.getId(), e.getStudent(), e.getCourse(), e.getAmountInCents())));
    }

    @Override public void deleteById(String id) {
        inTransaction(em -> {
            var enrollment = em.find(EnrollmentEntity.class, id);
            if (enrollment != null) em.remove(enrollment);
            var pixPayment = em.find(PendingPixPaymentEntity.class, id);
            if (pixPayment != null) em.remove(pixPayment);
            return null;
        });
    }

    @Override public void savePendingPix(PendingPixPayment payment) {
        inTransaction(em -> {
            var entity = new PendingPixPaymentEntity(payment.enrollmentId(), payment.student(), payment.course(),
                    payment.amountInCents(), payment.appMaxOrderId(), payment.providerStatus(), payment.qrCode(),
                    payment.emvCode(), payment.expiresAt());
            em.merge(entity);
            return null;
        });
    }

    @Override public Optional<PendingPixPayment> findPendingPix(String enrollmentId) {
        return read(em -> Optional.ofNullable(em.find(PendingPixPaymentEntity.class, enrollmentId)).map(this::toDomain));
    }

    @Override public void updatePendingPixStatus(String enrollmentId, String providerStatus) {
        inTransaction(em -> {
            var entity = em.find(PendingPixPaymentEntity.class, enrollmentId);
            if (entity == null) throw new IllegalArgumentException("Pending Pix payment not found: " + enrollmentId);
            entity.updateStatus(providerStatus);
            return null;
        });
    }

    /** Updates payment state and enrollment atomically in one JPA transaction. */
    @Override public void confirmPixEnrollment(String enrollmentId, String providerStatus) {
        var normalizedStatus = providerStatus == null ? "" : providerStatus.toLowerCase(java.util.Locale.ROOT);
        if (!normalizedStatus.equals("aprovado") && !normalizedStatus.equals("integrado"))
            throw new IllegalArgumentException("Only AppMax-approved Pix orders may confirm enrollment");
        inTransaction(em -> {
            var payment = em.find(PendingPixPaymentEntity.class, enrollmentId);
            if (payment == null) throw new IllegalArgumentException("Pending Pix payment not found: " + enrollmentId);
            payment.updateStatus(providerStatus);
            var enrollment = em.find(EnrollmentEntity.class, enrollmentId);
            if (enrollment == null) {
                em.persist(new EnrollmentEntity(enrollmentId, payment.getStudent(), payment.getCourse(), payment.getAmountInCents()));
            } else {
                enrollment.update(payment.getStudent(), payment.getCourse(), payment.getAmountInCents());
            }
            return null;
        });
    }

    private PendingPixPayment toDomain(PendingPixPaymentEntity e) {
        return new PendingPixPayment(e.getEnrollmentId(), e.getStudent(), e.getCourse(), e.getAmountInCents(),
                e.getAppMaxOrderId(), e.getProviderStatus(), e.getQrCode(), e.getEmvCode(), e.getExpiresAt());
    }

    private <T> T read(Function<EntityManager, T> work) {
        var em = entityManagerFactory.createEntityManager();
        try { return work.apply(em); }
        finally { em.close(); }
    }

    private <T> T inTransaction(Function<EntityManager, T> work) {
        var em = entityManagerFactory.createEntityManager();
        var transaction = em.getTransaction();
        try {
            transaction.begin();
            var result = work.apply(em);
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        } finally { em.close(); }
    }

    @Override public void close() { entityManagerFactory.close(); }
}
