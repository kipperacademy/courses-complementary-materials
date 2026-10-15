import java.util.List;

public class DemoEmail {
    public static void main(String[] args) {
        Matriculas matriculas = new Matriculas(new PagamentoLocal(), new Email(true));

        List<Pedido> pedidos = List.of(
            new Pedido("Ana", StatusPagamento.CONFIRMADO),
            new Pedido("Bia", StatusPagamento.CONFIRMADO),
            new Pedido("Clara", StatusPagamento.CONFIRMADO)
        );

        System.out.println("E-MAIL DISPONÍVEL");
        for (Pedido pedido : pedidos) {
            matriculas.confirmar(pedido);
        }

        System.out.println("Acessos liberados: " + matriculas.acessosLiberados() + "/3");
        System.out.println("Avisos pendentes: " + matriculas.avisosPendentes());
    }
}
