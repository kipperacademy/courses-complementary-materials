package br.com.kipperdev.clean.interfaceadapters.gateways;

import br.com.kipperdev.clean.usecases.PaymentProvider;
import br.com.kipperdev.clean.entities.PaymentStatus;

/** Traduz a conversa interna para o formato externo e normaliza a resposta do provedor. */
public final class AppMaxPaymentAdapter implements PaymentProvider {
    private final AppMaxApiSimulator appMax;

    public AppMaxPaymentAdapter(AppMaxApiSimulator appMax) {
        this.appMax = appMax;
    }

    @Override
    public PaymentResult charge(PaymentCommand command) {
        var external = appMax.createCharge(new AppMaxApiSimulator.Request(
                command.enrollmentId(), command.student(), command.course(), command.amountInCents()));
        return new PaymentResult(toInternalStatus(external.status()), external.paymentId(), null, null, null);
    }

    @Override public PaymentStatus verify(String paymentReference, int expectedAmountInCents) {
        // Demo adapter has no remote order lookup. Simulated paid references are confirmed.
        return paymentReference != null && paymentReference.startsWith("appmax-demo-enr-")
                ? PaymentStatus.CONFIRMED : PaymentStatus.PENDING;
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
