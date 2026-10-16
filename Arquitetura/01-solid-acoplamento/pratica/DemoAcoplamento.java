import java.io.IOException;
import java.util.List;

public class DemoAcoplamento {
    public static void main(String[] args) {
        boolean emailDisponivel = false;
        MatriculasAcopladas matriculas = new MatriculasAcopladas(emailDisponivel);

        List<Pedido> pedidos = List.of(
            new Pedido("Ana", StatusPagamento.CONFIRMADO),
            new Pedido("Bia", StatusPagamento.CONFIRMADO),
            new Pedido("Clara", StatusPagamento.CONFIRMADO)
        );

        System.out.println("E-MAIL FORA DO AR — VERSÃO ACOPLADA");
        for (Pedido pedido : pedidos) {
            try {
                matriculas.confirmar(pedido);
            } catch (IOException erro) {
                System.out.println(pedido.aluna() + ": " + erro.getMessage());
            }
        }

        System.out.println("Acessos liberados: " + matriculas.acessosLiberados() + "/3");
    }
}
