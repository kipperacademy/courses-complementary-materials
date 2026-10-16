package demos;

import adapters.saida.RepositorioEmMemoria;
import aplicacao.MatricularAluno;
import aplicacao.RepositorioMatriculas;
import dominio.StatusPagamento;

public class DemoCleanMemoria {
    public static void main(String[] args) {
        RepositorioMatriculas repositorio = new RepositorioEmMemoria();
        MatricularAluno matricular = new MatricularAluno(repositorio);

        matricular.executar("Ana", StatusPagamento.CONFIRMADO);
        matricular.executar("Bia", StatusPagamento.PENDENTE);
        matricular.executar("Clara", StatusPagamento.CONFIRMADO);

        System.out.println("Matrículas salvas: " + repositorio.listar());
        RepositorioMatriculas novaInstancia = new RepositorioEmMemoria();
        System.out.println("Nova instância: " + novaInstancia.listar());
    }
}
