public class DemoSemDesconto {
    public static void main(String[] args) {
        int precoCursoCentavos = 10_000;
        PoliticaPreco politicaPreco = new SemDesconto();
        Pagamento pagamento = PagamentoFactory.criar(Fornecedor.LEGADO);
        RegistroCompras registro = RegistroCompras.getInstance();
        Checkout checkout = new Checkout(politicaPreco, pagamento, registro);

        int confirmadoCentavos = checkout.comprar(precoCursoCentavos);

        System.out.println("Pagamento confirmado: " + confirmadoCentavos + " centavos");
    }
}
