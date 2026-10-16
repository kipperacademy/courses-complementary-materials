package demos;

import adapters.entrada.EntradaTerminal;
import adapters.saida.RepositorioEmMemoria;
import aplicacao.Matricular;
import aplicacao.MatricularAluno;
import aplicacao.RepositorioMatriculas;

public class DemoHexagonalTerminal {
    public static void main(String[] args) {
        RepositorioMatriculas repositorio = new RepositorioEmMemoria();
        Matricular matricular = new MatricularAluno(repositorio);
        EntradaTerminal terminal = new EntradaTerminal(matricular);

        terminal.receber(args);
        System.out.println("Matrículas salvas: " + repositorio.listar());
    }
}
