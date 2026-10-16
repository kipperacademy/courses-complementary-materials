package br.com.kipperdev.hexagonal.adapters.secondary;

import br.com.kipperdev.hexagonal.application.ports.secondary.PaymentProvider;
import br.com.kipperdev.hexagonal.domain.PaymentStatus;

/** Adapter traduz o contrato interno para a API do fornecedor e normaliza sua resposta. */
public final class AppMaxPaymentAdapter implements PaymentProvider {
    private final AppMaxApiSimulator appMax;

    public AppMaxPaymentAdapter(AppMaxApiSimulator appMax) { this.appMax = appMax; }

    @Override
    public PaymentResult charge(PaymentCommand command) {
        var external = appMax.createCharge(new AppMaxApiSimulator.Request(
                command.enrollmentId(), command.student(), command.course(), command.amountInCents()));
        return new PaymentResult(toInternalStatus(external.status()), external.paymentId());
    }

    private PaymentStatus toInternalStatus(String providerStatus) {
        return switch (providerStatus.toLowerCase()) {
            case "paid" -> PaymentStatus.CONFIRMED;
            case "pending" -> PaymentStatus.PENDING;
            case "refused" -> PaymentStatus.DECLINED;
            default -> PaymentStatus.UNKNOWN;
        };
    }
}
