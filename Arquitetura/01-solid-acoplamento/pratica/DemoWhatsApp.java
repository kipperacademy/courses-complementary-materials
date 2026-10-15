import java.util.List;

public class DemoWhatsApp {
    public static void main(String[] args) {
        Matriculas matriculas = new Matriculas(new PagamentoLocal(), new WhatsApp());

        List<Pedido> pedidos = List.of(
            new Pedido("Ana", StatusPagamento.CONFIRMADO),
            new Pedido("Bia", StatusPagamento.CONFIRMADO),
            new Pedido("Clara", StatusPagamento.CONFIRMADO)
        );

        System.out.println("AVISO POR WHATSAPP");
        for (Pedido pedido : pedidos) {
            matriculas.confirmar(pedido);
        }

        System.out.println("Acessos liberados: " + matriculas.acessosLiberados() + "/3");
        System.out.println("Avisos pendentes: " + matriculas.avisosPendentes());
    }
}
