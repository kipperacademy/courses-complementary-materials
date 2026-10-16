package br.com.kipperdev.clean.usecases;

import br.com.kipperdev.clean.entities.PaymentStatus;

/** Webhooks only trigger a lookup; verified API status is the authority for granting access. */
public final class ConfirmEnrollmentPayment {
    private final PaymentProvider paymentProvider;
    private final PendingPaymentRepository pendingPayments;
    public ConfirmEnrollmentPayment(PaymentProvider paymentProvider, PendingPaymentRepository pendingPayments) {
        this.paymentProvider = paymentProvider;
        this.pendingPayments = pendingPayments;
    }

    public void process(PendingPaymentRepository.WebhookEvent event) {
        if (!isConfirmationSignal(event.event())) return;
        var pending = pendingPayments.findByPaymentReference(event.orderId());
        if (pending.isEmpty()) {
            if (pendingPayments.isPaymentConfirmed(event.orderId()))
                pendingPayments.markWebhookProcessed(event.event(), event.orderId());
            else if (event.attemptCount() >= 2)
                pendingPayments.markWebhookProcessed(event.event(), event.orderId());
            else pendingPayments.deferWebhook(event.event(), event.orderId());
            return;
        }
        if (paymentProvider.verify(event.orderId(), pending.get().enrollment().amountInCents()) == PaymentStatus.CONFIRMED) {
            pendingPayments.confirm(pending.get(), event.event(), event.orderId());
        } else pendingPayments.deferWebhook(event.event(), event.orderId());
    }

    private boolean isConfirmationSignal(String event) {
        return "order_approved".equals(event) || "order_paid_by_pix".equals(event)
                || "order_integrated".equals(event);
    }
}
