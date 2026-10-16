import java.util.ArrayDeque;
import java.util.Queue;

public class Fila {
    private final Queue<Evento> eventos = new ArrayDeque<>();
    public void publicar(Evento evento) { eventos.add(evento); }
    public Evento proximo() { return eventos.peek(); }
    public void confirmar() { eventos.remove(); }
    public int tamanho() { return eventos.size(); }
}
