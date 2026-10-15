package br.com.kipperdev.hexagonal.application.usecases;

import br.com.kipperdev.hexagonal.application.ports.primary.CheckPixEnrollment;
import br.com.kipperdev.hexagonal.application.ports.secondary.EnrollmentRepository;
import br.com.kipperdev.hexagonal.application.ports.secondary.PixPaymentProvider;
import br.com.kipperdev.hexagonal.domain.Enrollment;
import br.com.kipperdev.hexagonal.domain.PaymentStatus;
import java.util.Optional;

public final class CheckPixEnrollmentService implements CheckPixEnrollment {
    private final PixPaymentProvider provider;
    private final EnrollmentRepository repository;
    public CheckPixEnrollmentService(PixPaymentProvider provider, EnrollmentRepository repository) {
        this.provider = provider; this.repository = repository;
    }
    @Override public Result check(String enrollmentId) {
        var pending = repository.findPendingPix(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("No Pix payment stored for enrollment " + enrollmentId));
        var providerState = provider.getOrderPaymentState(pending.appMaxOrderId());
        var status = providerState.status();
        var normalized = status.toLowerCase(java.util.Locale.ROOT);
        if (normalized.equals("aprovado") || normalized.equals("integrado")) {
            if (providerState.totalPaidInCents() == null || providerState.totalPaidInCents() != pending.amountInCents()) {
                repository.updatePendingPixStatus(enrollmentId, status);
                return new Result(PaymentStatus.UNKNOWN, status, providerState.totalPaidInCents(), Optional.empty());
            }
            repository.confirmPixEnrollment(enrollmentId, status);
            var enrollment = new Enrollment(enrollmentId, pending.student(), pending.course(), pending.amountInCents());
            return new Result(PaymentStatus.CONFIRMED, status, providerState.totalPaidInCents(), Optional.of(enrollment));
        }
        repository.updatePendingPixStatus(enrollmentId, status);
        var paymentStatus = switch (normalized) {
            case "pendente", "pendente_integracao", "pendente_integracao_em_analise", "autorizado" -> PaymentStatus.PENDING;
            case "cancelado", "estornado", "recusado_por_risco", "chargeback_em_tratativa", "chargeback_em_disputa", "chargeback_perdido", "chargeback_vencido" -> PaymentStatus.DECLINED;
            default -> PaymentStatus.UNKNOWN;
        };
        return new Result(paymentStatus, status, providerState.totalPaidInCents(), Optional.empty());
    }
}
