import java.util.List;

public class DemoDesacoplamento {
    public static void main(String[] args) {
        Notificador notificador = new Email(false);
        Matriculas matriculas = new Matriculas(new PagamentoLocal(), notificador);

        List<Pedido> pedidos = List.of(
            new Pedido("Ana", StatusPagamento.CONFIRMADO),
            new Pedido("Bia", StatusPagamento.CONFIRMADO),
            new Pedido("Clara", StatusPagamento.CONFIRMADO)
        );

        System.out.println("VERSÃO DESACOPLADA");
        for (Pedido pedido : pedidos) {
            matriculas.confirmar(pedido);
        }

        System.out.println("Acessos liberados: " + matriculas.acessosLiberados() + "/3");
        System.out.println("Avisos pendentes: " + matriculas.avisosPendentes());
    }
}
