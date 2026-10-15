public class DemoEntregaDuplicada {
    public static void main(String[] args) {
        Fila fila = new Fila();
        Notificacoes notificacoes = new Notificacoes();
        fila.publicar(new Evento("evento-1", "Ana"));
        notificacoes.registrar(fila.proximo());
        System.out.println("Worker parou antes do ACK. Mensagem continua na fila.");
        notificacoes.registrar(fila.proximo());
        fila.confirmar();
        System.out.println("Avisos registrados: " + notificacoes.avisos());
    }
}
