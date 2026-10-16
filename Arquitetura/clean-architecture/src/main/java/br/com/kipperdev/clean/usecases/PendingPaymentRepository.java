package br.com.kipperdev.clean.usecases;

import br.com.kipperdev.clean.entities.Enrollment;
import java.util.List;
import java.util.Optional;

/** Persistence ports for payment sessions and the durable webhook inbox. */
public interface PendingPaymentRepository {
    void save(PendingEnrollment pending);
    Optional<PendingEnrollment> findByPaymentReference(String reference);
    Optional<PendingEnrollment> findByEnrollmentId(String enrollmentId);
    boolean isPaymentConfirmed(String reference);
    void remove(String reference);
    void confirm(PendingEnrollment pending, String event, String orderId);
    void enqueueWebhook(String event, String orderId, String rawPayload);
    List<WebhookEvent> pendingWebhookEvents();
    void markWebhookProcessed(String event, String orderId);
    void deferWebhook(String event, String orderId);

    record PendingEnrollment(Enrollment enrollment, String paymentReference,
                             String pixEmv, String pixQrCode, String pixExpiration) {}
    record WebhookEvent(String event, String orderId, String rawPayload, int attemptCount) {}
}
