public class DescontoPix implements PoliticaPreco {
    public int calcular(int precoCentavos) {
        return precoCentavos * 90 / 100;
    }
}
