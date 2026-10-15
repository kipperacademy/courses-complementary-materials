package br.com.kipperdev.hexagonal.application.ports.primary;

import br.com.kipperdev.hexagonal.domain.Enrollment;
import br.com.kipperdev.hexagonal.domain.PaymentStatus;
import java.util.Optional;

/** Porta de entrada: capacidade que o núcleo oferece a agentes externos. */
public interface EnrollStudent {
    Result enroll(Command command);

    record Command(String id, String student, String course, int amountInCents) {}
    record Result(PaymentStatus paymentStatus, String paymentReference, Optional<Enrollment> enrollment) {}
}
