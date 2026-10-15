package br.com.kipperdev.hexagonal.application.ports.primary;

import br.com.kipperdev.hexagonal.domain.PendingPixPayment;

public interface CreatePixEnrollment {
    PendingPixPayment create(Command command);
    record Command(String enrollmentId, String firstName, String lastName, String email,
                   String phone, String ip, String documentNumber, String student,
                   String course, int amountInCents) {}
}
