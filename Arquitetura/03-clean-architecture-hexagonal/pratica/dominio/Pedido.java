package dominio;

public record Pedido(String aluna, StatusPagamento pagamento) {
    public boolean permiteMatricula() {
        return pagamento == StatusPagamento.CONFIRMADO;
    }
}
