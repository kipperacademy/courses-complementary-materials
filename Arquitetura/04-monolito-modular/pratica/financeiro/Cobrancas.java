package financeiro;

import java.util.HashSet;
import java.util.Set;

public final class Cobrancas {
    private final Set<String> confirmadas = new HashSet<>();

    public void confirmar(String pedido) {
        confirmadas.add(pedido);
    }

    public boolean estaConfirmado(String pedido) {
        return confirmadas.contains(pedido);
    }
}
