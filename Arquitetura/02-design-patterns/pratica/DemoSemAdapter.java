import java.math.BigDecimal;

public class DemoSemAdapter {
    public static void main(String[] args) {
        int totalCentavos = 9_000;
        SdkLegado sdk = new SdkLegado();
        System.out.println("Intenção: " + totalCentavos + " centavos = R$ 90.00");

        BigDecimal confirmadoReais = sdk.charge(Integer.toString(totalCentavos), "BRL");

        System.out.println("Valor interpretado pelo SDK: R$ " + confirmadoReais);
    }
}
