import java.io.IOException;

final class PagamentoLocal implements ConsultaPagamento {
    public StatusPagamento consultar(Pedido pedido) {
        return pedido.pagamento();
    }
}

record Email(boolean disponivel) implements Notificador {
    public void enviar(String aluna) throws IOException {
        if (!disponivel) throw new IOException("E-mail fora do ar");
        System.out.println("  E-mail enviado para " + aluna);
    }
}

final class WhatsApp implements Notificador {
    public void enviar(String aluna) {
        System.out.println("  WhatsApp enviado para " + aluna);
    }
}
