import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Notificacoes {
    private final List<String> avisos = new ArrayList<>();
    private final Set<String> processados = new HashSet<>();

    public void registrar(Evento evento) { avisos.add(evento.aluna()); }

    public void registrarUmaVez(Evento evento) {
        if (processados.contains(evento.id())) return;
        avisos.add(evento.aluna());
        processados.add(evento.id());
    }

    public List<String> avisos() { return List.copyOf(avisos); }
}
