package br.com.kipperdev.hexagonal.application.ports.secondary;

import br.com.kipperdev.hexagonal.domain.PaymentStatus;

/** Porta de saída: conversa de pagamento necessária ao caso de uso. */
public interface PaymentProvider {
    PaymentResult charge(PaymentCommand command);

    record PaymentCommand(String enrollmentId, String student, String course, int amountInCents) {}
    record PaymentResult(PaymentStatus status, String reference) {}
}
