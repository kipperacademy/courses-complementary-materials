public final class RegistroCompras {
    private static final RegistroCompras INSTANCIA = new RegistroCompras();
    private int quantidade;

    private RegistroCompras() {}

    public static RegistroCompras getInstance() {
        return INSTANCIA;
    }

    public void registrar() {
        quantidade++;
    }

    public int quantidade() {
        return quantidade;
    }
}
