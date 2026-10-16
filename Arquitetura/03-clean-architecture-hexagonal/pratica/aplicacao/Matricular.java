package aplicacao;

import dominio.StatusPagamento;

public interface Matricular {
    boolean executar(String aluna, StatusPagamento pagamento);
}
