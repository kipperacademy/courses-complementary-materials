package aplicacao;

import java.util.List;

public interface RepositorioMatriculas {
    void salvar(String aluna);
    List<String> listar();
}
