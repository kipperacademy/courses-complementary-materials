package demos;

import adapters.entrada.EntradaCsv;
import adapters.saida.RepositorioEmMemoria;
import aplicacao.Matricular;
import aplicacao.MatricularAluno;
import aplicacao.RepositorioMatriculas;
public class DemoHexagonalCsv {
    public static void main(String[] args) {
        RepositorioMatriculas repositorio = new RepositorioEmMemoria();
        Matricular matricular = new MatricularAluno(repositorio);
        EntradaCsv csv = new EntradaCsv(matricular);

        csv.importar("Ana;CONFIRMADO\nBia;PENDENTE");
        System.out.println("Matrículas salvas: " + repositorio.listar());
    }
}
