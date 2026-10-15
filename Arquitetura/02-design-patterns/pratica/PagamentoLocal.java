public class PagamentoLocal implements Pagamento {
    public int cobrar(int valorCentavos) {
        System.out.println("Pagamento local recebeu: " + valorCentavos + " centavos");
        return valorCentavos;
    }
}
