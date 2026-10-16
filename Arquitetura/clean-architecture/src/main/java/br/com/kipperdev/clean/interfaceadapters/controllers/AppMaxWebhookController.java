package br.com.kipperdev.clean.interfaceadapters.controllers;

import br.com.kipperdev.clean.usecases.ConfirmEnrollmentPayment;
import br.com.kipperdev.clean.usecases.PendingPaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** Durable webhook inbox. The event is a trigger only; confirmation always reads the AppMax API. */
public final class AppMaxWebhookController implements AutoCloseable {
    private static final int MAX_BODY_BYTES = 64 * 1024;
    private final HttpServer server;
    private final ScheduledExecutorService worker;
    private final PendingPaymentRepository inbox;
    private final ConfirmEnrollmentPayment confirmation;
    private final ObjectMapper json = new ObjectMapper();

    public AppMaxWebhookController(int port, PendingPaymentRepository inbox,
                               ConfirmEnrollmentPayment confirmation) throws IOException {
        this.inbox = inbox;
        this.confirmation = confirmation;
        this.server = HttpServer.create(new InetSocketAddress(port), 64);
        this.server.createContext("/webhooks/appmax", this::receive);
        this.worker = Executors.newSingleThreadScheduledExecutor(r -> {
            var thread = new Thread(r, "appmax-webhook-worker");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void start() {
        server.start();
        worker.scheduleWithFixedDelay(this::processInbox, 0, 10, TimeUnit.SECONDS);
    }

    public int port() { return server.getAddress().getPort(); }

    private void receive(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            respond(exchange, 405, "{\"error\":\"POST required\"}");
            return;
        }
        var contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.toLowerCase().startsWith("application/json")) {
            respond(exchange, 415, "{\"error\":\"application/json required\"}");
            return;
        }
        byte[] bytes = exchange.getRequestBody().readNBytes(MAX_BODY_BYTES + 1);
        if (bytes.length > MAX_BODY_BYTES) {
            respond(exchange, 413, "{\"error\":\"payload too large\"}");
            return;
        }
        String raw = new String(bytes, StandardCharsets.UTF_8);
        final JsonNode payload;
        try { payload = json.readTree(raw); }
        catch (Exception e) {
            respond(exchange, 400, "{\"error\":\"invalid webhook payload\"}");
            return;
        }
        try {
            String event = payload.path("event").asText();
            String eventType = payload.path("event_type").asText();
            JsonNode data = payload.path("data");
            JsonNode orderId = data.path("order").path("id");
            if (orderId.isMissingNode() || orderId.isNull()) orderId = data.path("order_id");
            if (!"order".equals(eventType) || !isConfirmationSignal(event)
                    || orderId.isMissingNode() || orderId.isNull() || !orderId.asText().matches("[0-9]+")) {
                respond(exchange, 202, "{\"received\":true,\"queued\":false}");
                return;
            }
            // AppMax sends no HMAC/auth header. Persist the raw signal and return quickly;
            // only a subsequently verified GET /v1/orders/{id} can grant access.
            inbox.enqueueWebhook(event, orderId.asText(), raw);
            respond(exchange, 202, "{\"received\":true,\"queued\":true}");
        } catch (RuntimeException e) {
            respond(exchange, 503, "{\"error\":\"webhook could not be queued\"}");
        }
    }

    private void processInbox() {
        try {
            for (var event : inbox.pendingWebhookEvents()) {
                try { confirmation.process(event); }
                catch (RuntimeException e) {
                    try { inbox.deferWebhook(event.event(), event.orderId()); } catch (RuntimeException ignored) { }
                    System.err.println("AppMax webhook verification deferred: " + e.getMessage());
                }
            }
        } catch (RuntimeException e) {
            System.err.println("Could not read AppMax webhook inbox: " + e.getMessage());
        }
    }

    private boolean isConfirmationSignal(String event) {
        return "order_approved".equals(event) || "order_paid_by_pix".equals(event)
                || "order_integrated".equals(event);
    }

    private void respond(HttpExchange exchange, int status, String body) throws IOException {
        var bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var output = exchange.getResponseBody()) { output.write(bytes); }
    }

    @Override public void close() {
        worker.shutdownNow();
        server.stop(1);
    }
}
