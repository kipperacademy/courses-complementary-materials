import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

final class Matriculas {
    private final ConsultaPagamento pagamentos;
    private final Notificador notificador;
    private final Set<String> acessos = new HashSet<>();
    private final Set<String> avisosPendentes = new HashSet<>();

    Matriculas(ConsultaPagamento pagamentos, Notificador notificador) {
        this.pagamentos = pagamentos;
        this.notificador = notificador;
    }

    void confirmar(Pedido pedido) {
        if (pagamentos.consultar(pedido) != StatusPagamento.CONFIRMADO) return;
        acessos.add(pedido.aluna());
        try {
            notificador.enviar(pedido.aluna());
            avisosPendentes.remove(pedido.aluna());
        } catch (IOException erro) {
            avisosPendentes.add(pedido.aluna());
        }
    }

    int acessosLiberados() {
        return acessos.size();
    }

    int avisosPendentes() {
        return avisosPendentes.size();
    }
}
