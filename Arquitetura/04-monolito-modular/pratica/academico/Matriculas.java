package academico;

import financeiro.Cobrancas;
import java.util.ArrayList;
import java.util.List;

public final class Matriculas {
    private final Cobrancas cobrancas;
    private final List<String> alunas = new ArrayList<>();

    public Matriculas(Cobrancas cobrancas) {
        this.cobrancas = cobrancas;
    }

    public void matricular(String pedido, String aluna) {
        if (cobrancas.estaConfirmado(pedido)) alunas.add(aluna);
    }

    public List<String> listar() {
        return List.copyOf(alunas);
    }
}
