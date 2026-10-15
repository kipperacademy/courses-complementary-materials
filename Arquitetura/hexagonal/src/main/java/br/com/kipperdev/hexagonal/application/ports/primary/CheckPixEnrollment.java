package br.com.kipperdev.hexagonal.application.ports.primary;

import br.com.kipperdev.hexagonal.domain.Enrollment;
import br.com.kipperdev.hexagonal.domain.PaymentStatus;
import java.util.Optional;

public interface CheckPixEnrollment {
    Result check(String enrollmentId);
    record Result(PaymentStatus paymentStatus, String providerStatus, Long totalPaidInCents,
                  Optional<Enrollment> enrollment) {}
}
