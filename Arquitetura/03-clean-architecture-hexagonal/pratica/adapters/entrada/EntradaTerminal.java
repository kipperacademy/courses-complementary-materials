package adapters.entrada;

import aplicacao.Matricular;
import dominio.StatusPagamento;

public class EntradaTerminal {
    private final Matricular matricular;

    public EntradaTerminal(Matricular matricular) {
        this.matricular = matricular;
    }

    public void receber(String[] argumentos) {
        if (argumentos.length != 2) {
            throw new IllegalArgumentException("Use: nome CONFIRMADO|PENDENTE");
        }
        String aluna = argumentos[0];
        StatusPagamento pagamento = StatusPagamento.valueOf(argumentos[1]);
        boolean liberada = matricular.executar(aluna, pagamento);
        System.out.println(aluna + " | matrícula liberada: " + liberada);
    }
}
