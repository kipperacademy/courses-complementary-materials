import java.io.IOException;

enum StatusPagamento { CONFIRMADO, PENDENTE }

record Pedido(String aluna, StatusPagamento pagamento) {}

interface ConsultaPagamento {
    StatusPagamento consultar(Pedido pedido);
}

interface Notificador {
    void enviar(String aluna) throws IOException;
}
