public class Checkout {
    private final PoliticaPreco politicaPreco;
    private final Pagamento pagamento;
    private final RegistroCompras registro;

    public Checkout(PoliticaPreco politicaPreco, Pagamento pagamento, RegistroCompras registro) {
        this.politicaPreco = politicaPreco;
        this.pagamento = pagamento;
        this.registro = registro;
    }

    public int comprar(int precoCentavos) {
        int totalCentavos = politicaPreco.calcular(precoCentavos);
        System.out.println("Total calculado: " + totalCentavos + " centavos");
        int confirmadoCentavos = pagamento.cobrar(totalCentavos);
        registro.registrar();
        return confirmadoCentavos;
    }
}
