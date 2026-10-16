package adapters.saida;

import aplicacao.RepositorioMatriculas;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RepositorioEmMemoria implements RepositorioMatriculas {
    private final Set<String> alunas = new LinkedHashSet<>();

    public void salvar(String aluna) {
        alunas.add(aluna);
    }

    public List<String> listar() {
        return List.copyOf(alunas);
    }
}
