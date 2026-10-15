package adapters.entrada;

import aplicacao.Matricular;
import dominio.StatusPagamento;

public class EntradaCsv {
    private final Matricular matricular;

    public EntradaCsv(Matricular matricular) {
        this.matricular = matricular;
    }

    public void importar(String conteudoCsv) {
        for (String linha : conteudoCsv.lines().toList()) {
            String[] campos = linha.split(";", -1);
            if (campos.length != 2) {
                throw new IllegalArgumentException("Linha inválida: " + linha);
            }
            String aluna = campos[0].trim();
            StatusPagamento pagamento = StatusPagamento.valueOf(campos[1].trim());
            boolean liberada = matricular.executar(aluna, pagamento);
            System.out.println(aluna + " | matrícula liberada: " + liberada);
        }
    }
}
