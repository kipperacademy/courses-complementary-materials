package br.com.kipperdev.hexagonal.application.ports.secondary;

/** Pix-only boundary for creating a payment and querying the provider's authoritative order state. */
public interface PixPaymentProvider {
    PixCharge create(PixCommand command);
    OrderPaymentState getOrderPaymentState(String appMaxOrderId);

    record PixCommand(String enrollmentId, String firstName, String lastName, String email,
                      String phone, String ip, String documentNumber, String student,
                      String course, int amountInCents) {}
    record PixCharge(String orderId, String status, String qrCode, String emvCode, String expiresAt) {}
    /** totalPaidInCents may be absent for an unpaid order; it is mandatory for enrollment confirmation. */
    record OrderPaymentState(String status, Long totalPaidInCents) {}
}
