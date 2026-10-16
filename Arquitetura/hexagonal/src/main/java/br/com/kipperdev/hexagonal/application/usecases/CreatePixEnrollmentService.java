package br.com.kipperdev.hexagonal.application.usecases;

import br.com.kipperdev.hexagonal.application.ports.primary.CreatePixEnrollment;
import br.com.kipperdev.hexagonal.application.ports.secondary.EnrollmentRepository;
import br.com.kipperdev.hexagonal.application.ports.secondary.PixPaymentProvider;
import br.com.kipperdev.hexagonal.domain.PendingPixPayment;

public final class CreatePixEnrollmentService implements CreatePixEnrollment {
    private final PixPaymentProvider provider;
    private final EnrollmentRepository repository;
    public CreatePixEnrollmentService(PixPaymentProvider provider, EnrollmentRepository repository) {
        this.provider = provider; this.repository = repository;
    }
    @Override public PendingPixPayment create(Command c) {
        if (c.amountInCents() <= 0) throw new IllegalArgumentException("Amount must be positive cents");
        if (repository.findPendingPix(c.enrollmentId()).isPresent())
            throw new IllegalArgumentException("A Pix payment already exists for this enrollment; use pix-check");
        var charge = provider.create(new PixPaymentProvider.PixCommand(c.enrollmentId(), c.firstName(), c.lastName(),
                c.email(), c.phone(), c.ip(), c.documentNumber(), c.student(), c.course(), c.amountInCents()));
        var pending = new PendingPixPayment(c.enrollmentId(), c.student(), c.course(), c.amountInCents(),
                charge.orderId(), charge.status(), charge.qrCode(), charge.emvCode(), charge.expiresAt());
        repository.savePendingPix(pending);
        return pending;
    }
}
