package br.com.kipperdev.clean.usecases;

import br.com.kipperdev.clean.entities.PaymentStatus;

/** Port defined in the application's language; adapters translate provider protocols to it. */
public interface PaymentProvider {
    PaymentResult charge(PaymentCommand command);
    PaymentStatus verify(String paymentReference, int expectedAmountInCents);

    record CustomerProfile(String firstName, String lastName, String email, String phone,
                           String ip, String documentNumber) {}
    record PaymentCommand(String enrollmentId, String student, String course, int amountInCents,
                          CustomerProfile customer) {}
    record PaymentResult(PaymentStatus status, String reference, String pixEmv, String pixQrCode,
                         String pixExpiration) {}
}
