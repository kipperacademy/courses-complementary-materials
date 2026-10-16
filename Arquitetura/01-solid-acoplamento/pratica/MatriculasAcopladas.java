import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

final class MatriculasAcopladas {
    private final PagamentoLocal pagamentos = new PagamentoLocal();
    private final Email email;
    private final Set<String> acessos = new HashSet<>();

    MatriculasAcopladas(boolean emailDisponivel) {
        this.email = new Email(emailDisponivel);
    }

    void confirmar(Pedido pedido) throws IOException {
        if (pagamentos.consultar(pedido) != StatusPagamento.CONFIRMADO) return;
        email.enviar(pedido.aluna());
        acessos.add(pedido.aluna());
    }

    int acessosLiberados() {
        return acessos.size();
    }
}
