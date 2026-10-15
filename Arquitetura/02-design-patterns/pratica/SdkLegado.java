import java.math.BigDecimal;

public class SdkLegado {
    public BigDecimal charge(String amount, String currency) {
        if (!currency.equals("BRL")) {
            throw new IllegalArgumentException("Esta simulação aceita somente BRL");
        }
        BigDecimal reais = new BigDecimal(amount);
        System.out.println("SDK simulado recebeu: R$ " + reais.setScale(2));
        return reais;
    }
}
