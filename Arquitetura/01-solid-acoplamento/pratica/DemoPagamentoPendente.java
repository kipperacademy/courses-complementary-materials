import java.util.List;

public class DemoPagamentoPendente {
    public static void main(String[] args) {
        Matriculas matriculas = new Matriculas(new PagamentoLocal(), new Email(true));

        List<Pedido> pedidos = List.of(
            new Pedido("Ana", StatusPagamento.PENDENTE),
            new Pedido("Bia", StatusPagamento.PENDENTE),
            new Pedido("Clara", StatusPagamento.PENDENTE)
        );

        System.out.println("PAGAMENTO PENDENTE");
        for (Pedido pedido : pedidos) {
            matriculas.confirmar(pedido);
        }

        System.out.println("Acessos liberados: " + matriculas.acessosLiberados() + "/3");
        System.out.println("Avisos pendentes: " + matriculas.avisosPendentes());
    }
}
