package br.com.kipperdev.hexagonal.application.usecases;

import br.com.kipperdev.hexagonal.application.ports.primary.EnrollStudent;
import br.com.kipperdev.hexagonal.application.ports.secondary.EnrollmentRepository;
import br.com.kipperdev.hexagonal.application.ports.secondary.PaymentProvider;
import br.com.kipperdev.hexagonal.domain.Enrollment;
import br.com.kipperdev.hexagonal.domain.PaymentStatus;
import java.util.Optional;

/** Caso de uso que implementa a porta de entrada. */
public final class EnrollStudentService implements EnrollStudent {
    private final PaymentProvider paymentProvider;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollStudentService(PaymentProvider paymentProvider, EnrollmentRepository enrollmentRepository) {
        this.paymentProvider = paymentProvider;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Result enroll(Command command) {
        // Validate the command before contacting a payment provider.
        new Enrollment(command.id(), command.student(), command.course(), command.amountInCents());
        var payment = paymentProvider.charge(new PaymentProvider.PaymentCommand(
                command.id(), command.student(), command.course(), command.amountInCents()));
        if (payment.status() != PaymentStatus.CONFIRMED) {
            return new Result(payment.status(), payment.reference(), Optional.empty());
        }

        var enrollment = new Enrollment(command.id(), command.student(), command.course(), command.amountInCents());
        enrollmentRepository.save(enrollment);
        return new Result(payment.status(), payment.reference(), Optional.of(enrollment));
    }
}
