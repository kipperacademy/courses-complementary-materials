package br.com.kipperdev.clean.interfaceadapters.gateways;

/** Simula apenas a resposta externa. Não abre conexão nem processa dados de cartão. */
public final class AppMaxApiSimulator {
    private final java.util.Map<String, String> statusesByOrder;

    public AppMaxApiSimulator(java.util.Map<String, String> statusesByOrder) {
        this.statusesByOrder = java.util.Map.copyOf(statusesByOrder);
    }

    public Response createCharge(Request request) {
        System.out.printf("[AppMax simulada] cobrança de %s centavos para pedido %s%n",
                request.amountInCents(), request.orderId());
        return new Response(statusesByOrder.getOrDefault(request.orderId(), "paid"),
                "appmax-demo-" + request.orderId());
    }

    public record Request(String orderId, String customerName, String productName, int amountInCents) {}
    public record Response(String status, String paymentId) {}
}
