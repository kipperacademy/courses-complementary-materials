public class PagamentoFactory {
    public static Pagamento criar(Fornecedor fornecedor) {
        return switch (fornecedor) {
            case LEGADO -> new PagamentoLegadoAdapter(new SdkLegado());
            case LOCAL -> new PagamentoLocal();
        };
    }
}
