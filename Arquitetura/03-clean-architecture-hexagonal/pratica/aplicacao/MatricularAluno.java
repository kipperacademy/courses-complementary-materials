package aplicacao;

import dominio.Pedido;
import dominio.StatusPagamento;

public class MatricularAluno implements Matricular {
    private final RepositorioMatriculas repositorio;

    public MatricularAluno(RepositorioMatriculas repositorio) {
        this.repositorio = repositorio;
    }

    public boolean executar(String aluna, StatusPagamento pagamento) {
        Pedido pedido = new Pedido(aluna, pagamento);
        if (!pedido.permiteMatricula()) return false;

        repositorio.salvar(aluna);
        return true;
    }
}
