package demos;

import adapters.saida.RepositorioEmArquivo;
import aplicacao.MatricularAluno;
import aplicacao.RepositorioMatriculas;
import dominio.StatusPagamento;
import java.nio.file.Path;

public class DemoCleanArquivo {
    public static void main(String[] args) {
        Path arquivo = Path.of("dados", "matriculas.txt");
        RepositorioMatriculas repositorio = new RepositorioEmArquivo(arquivo);
        MatricularAluno matricular = new MatricularAluno(repositorio);

        matricular.executar("Ana", StatusPagamento.CONFIRMADO);
        matricular.executar("Bia", StatusPagamento.PENDENTE);
        matricular.executar("Clara", StatusPagamento.CONFIRMADO);

        System.out.println("Matrículas salvas: " + repositorio.listar());
        RepositorioMatriculas novaInstancia = new RepositorioEmArquivo(arquivo);
        System.out.println("Nova instância: " + novaInstancia.listar());
        System.out.println("Arquivo real: " + arquivo.toAbsolutePath());
    }
}
