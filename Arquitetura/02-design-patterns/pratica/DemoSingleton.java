public class DemoSingleton {
    public static void main(String[] args) {
        RegistroCompras registroAna = RegistroCompras.getInstance();
        RegistroCompras registroBia = RegistroCompras.getInstance();
        System.out.println("Mesmo objeto? " + (registroAna == registroBia));

        Pagamento pagamentoAna = PagamentoFactory.criar(Fornecedor.LEGADO);
        Checkout compraAna = new Checkout(new DescontoPix(), pagamentoAna, registroAna);
        Pagamento pagamentoBia = PagamentoFactory.criar(Fornecedor.LOCAL);
        Checkout compraBia = new Checkout(new SemDesconto(), pagamentoBia, registroBia);

        compraAna.comprar(10_000);
        compraBia.comprar(10_000);

        System.out.println("Compras vistas por Ana: " + registroAna.quantidade());
        System.out.println("Compras vistas por Bia: " + registroBia.quantidade());
    }
}
