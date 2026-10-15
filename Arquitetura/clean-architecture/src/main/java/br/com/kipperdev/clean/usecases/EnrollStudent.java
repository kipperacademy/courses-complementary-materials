package br.com.kipperdev.clean.usecases;

import br.com.kipperdev.clean.entities.Enrollment;
import br.com.kipperdev.clean.entities.PaymentStatus;
import java.util.Optional;

/** Orquestra cobrança e matrícula; só a situação CONFIRMED libera acesso. */
public final class EnrollStudent {
    private final PaymentProvider paymentProvider;
    private final EnrollmentRepository enrollmentRepository;
    private final PendingPaymentRepository pendingPaymentRepository;

    public EnrollStudent(PaymentProvider paymentProvider, EnrollmentRepository enrollmentRepository,
                         PendingPaymentRepository pendingPaymentRepository) {
        this.paymentProvider = paymentProvider;
        this.enrollmentRepository = enrollmentRepository;
        this.pendingPaymentRepository = pendingPaymentRepository;
    }

    public Result execute(Request request) {
        // Validate the command before contacting a payment provider.
        new Enrollment(request.id(), request.student(), request.course(), request.amountInCents());
        var existing = enrollmentRepository.findAll().stream()
                .filter(enrollment -> enrollment.id().equals(request.id())).findFirst();
        if (existing.isPresent()) {
            return new Result(PaymentStatus.CONFIRMED, null, existing, null, null, null);
        }
        if (pendingPaymentRepository != null) {
            var pending = pendingPaymentRepository.findByEnrollmentId(request.id());
            if (pending.isPresent()) {
                var stored = pending.get();
                return new Result(PaymentStatus.PENDING, stored.paymentReference(), Optional.empty(),
                        stored.pixEmv(), stored.pixQrCode(), stored.pixExpiration());
            }
        }
        var payment = paymentProvider.charge(new PaymentProvider.PaymentCommand(
                request.id(), request.student(), request.course(), request.amountInCents(), request.customer()));
        if (payment.status() != PaymentStatus.CONFIRMED) {
            if (payment.status() == PaymentStatus.PENDING && pendingPaymentRepository != null
                    && payment.reference() != null && !payment.reference().isBlank()) {
                var enrollment = new Enrollment(request.id(), request.student(), request.course(), request.amountInCents());
                pendingPaymentRepository.save(new PendingPaymentRepository.PendingEnrollment(enrollment,
                        payment.reference(), payment.pixEmv(), payment.pixQrCode(), payment.pixExpiration()));
            }
            return new Result(payment.status(), payment.reference(), Optional.empty(), payment.pixEmv(),
                    payment.pixQrCode(), payment.pixExpiration());
        }

        var enrollment = new Enrollment(request.id(), request.student(), request.course(), request.amountInCents());
        enrollmentRepository.save(enrollment);
        return new Result(payment.status(), payment.reference(), Optional.of(enrollment), null, null, null);
    }

    public record Request(String id, String student, String course, int amountInCents,
                          PaymentProvider.CustomerProfile customer) {}
    public record Result(PaymentStatus paymentStatus, String paymentReference, Optional<Enrollment> enrollment,
                         String pixEmv, String pixQrCode, String pixExpiration) {}
}
