import academico.Matriculas;
import financeiro.Cobrancas;

public class DemoModulos {
    public static void main(String[] args) {
        Cobrancas cobrancas = new Cobrancas();
        Matriculas matriculas = new Matriculas(cobrancas);

        cobrancas.confirmar("pedido-ana");
        matriculas.matricular("pedido-ana", "Ana");
        matriculas.matricular("pedido-bia", "Bia");

        System.out.println("Matrículas: " + matriculas.listar());
    }
}
