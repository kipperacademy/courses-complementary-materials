import java.math.BigDecimal;

public class PagamentoLegadoAdapter implements Pagamento {
    private final SdkLegado sdk;

    public PagamentoLegadoAdapter(SdkLegado sdk) {
        this.sdk = sdk;
    }

    public int cobrar(int valorCentavos) {
        String valorReais = BigDecimal.valueOf(valorCentavos, 2).toPlainString();
        BigDecimal confirmadoReais = sdk.charge(valorReais, "BRL");
        return confirmadoReais.movePointRight(2).intValueExact();
    }
}
