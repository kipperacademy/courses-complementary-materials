package br.com.kipperdev.clean.interfaceadapters.controllers;

import br.com.kipperdev.clean.usecases.EnrollStudent;
import br.com.kipperdev.clean.usecases.ManageEnrollments;
import br.com.kipperdev.clean.usecases.PaymentProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;

/** Primary HTTP controller for enrollment CRUD requests. */
public final class EnrollmentHttpController implements AutoCloseable {
    private static final String COLLECTION_PATH = "/api/enrollments";
    private static final int MAX_BODY_BYTES = 64 * 1024;

    private final HttpServer server;
    private final EnrollStudent enrollStudent;
    private final ManageEnrollments manageEnrollments;
    private final PaymentProvider.CustomerProfile customerProfile;
    private final ObjectMapper json = new ObjectMapper();

    public EnrollmentHttpController(int port, EnrollStudent enrollStudent,
                                    ManageEnrollments manageEnrollments,
                                    PaymentProvider.CustomerProfile customerProfile) throws IOException {
        this.enrollStudent = enrollStudent;
        this.manageEnrollments = manageEnrollments;
        this.customerProfile = customerProfile;
        this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 64);
        this.server.createContext(COLLECTION_PATH, this::handle);
    }

    public void start() { server.start(); }
    public int port() { return server.getAddress().getPort(); }

    private void handle(HttpExchange exchange) throws IOException {
        var path = exchange.getRequestURI().getPath();
        var method = exchange.getRequestMethod();
        if (path.equals(COLLECTION_PATH) || path.equals(COLLECTION_PATH + "/")) {
            if ("GET".equals(method)) {
                respondJson(exchange, 200, manageEnrollments.list());
            } else if ("POST".equals(method)) {
                create(exchange);
            } else {
                methodNotAllowed(exchange, "GET, POST");
            }
            return;
        }

        if (!path.startsWith(COLLECTION_PATH + "/")) {
            respondError(exchange, 404, "route not found");
            return;
        }
        var id = path.substring((COLLECTION_PATH + "/").length());
        if (id.isBlank() || id.contains("/")) {
            respondError(exchange, 404, "enrollment not found");
            return;
        }
        switch (method) {
            case "GET" -> manageEnrollments.find(id)
                    .ifPresentOrElse(enrollment -> uncheckedJson(exchange, 200, enrollment),
                            () -> uncheckedError(exchange, 404, "enrollment not found"));
            case "PUT" -> update(exchange, id);
            case "DELETE" -> {
                if (manageEnrollments.delete(id)) respondEmpty(exchange, 204);
                else respondError(exchange, 404, "enrollment not found");
            }
            default -> methodNotAllowed(exchange, "GET, PUT, DELETE");
        }
    }

    private void create(HttpExchange exchange) throws IOException {
        if (!requireJson(exchange)) return;
        try {
            var request = readBody(exchange, CreateRequest.class);
            if (manageEnrollments.find(request.id()).isPresent()) {
                respondError(exchange, 409, "enrollment already exists");
                return;
            }
            var result = enrollStudent.execute(new EnrollStudent.Request(request.id(), request.student(),
                    request.course(), request.amountInCents(), customerProfile));
            var response = new LinkedHashMap<String, Object>();
            response.put("paymentStatus", result.paymentStatus());
            response.put("paymentReference", result.paymentReference());
            result.enrollment().ifPresent(value -> response.put("enrollment", value));
            response.put("pixEmv", result.pixEmv());
            response.put("pixQrCode", result.pixQrCode());
            response.put("pixExpiration", result.pixExpiration());
            int status = switch (result.paymentStatus()) {
                case CONFIRMED -> 201;
                case PENDING -> 202;
                case DECLINED -> 402;
                case UNKNOWN -> 502;
            };
            respondJson(exchange, status, response);
        } catch (IllegalArgumentException e) {
            respondError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            respondError(exchange, 502, "payment could not be processed");
        }
    }

    private void update(HttpExchange exchange, String id) throws IOException {
        if (!requireJson(exchange)) return;
        try {
            var request = readBody(exchange, UpdateRequest.class);
            var updated = manageEnrollments.updateDetails(id, request.student(), request.course());
            if (updated.isPresent()) respondJson(exchange, 200, updated.get());
            else respondError(exchange, 404, "enrollment not found");
        } catch (IllegalArgumentException e) {
            respondError(exchange, 400, e.getMessage());
        }
    }

    private boolean requireJson(HttpExchange exchange) throws IOException {
        var contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.toLowerCase().startsWith("application/json")) {
            respondError(exchange, 415, "application/json required");
            return false;
        }
        return true;
    }

    private <T> T readBody(HttpExchange exchange, Class<T> type) throws IOException {
        var bytes = exchange.getRequestBody().readNBytes(MAX_BODY_BYTES + 1);
        if (bytes.length > MAX_BODY_BYTES) throw new IllegalArgumentException("request body too large");
        try {
            var value = json.readValue(bytes, type);
            if (value == null) throw new IllegalArgumentException("JSON body is required");
            return value;
        }
        catch (IOException e) { throw new IllegalArgumentException("invalid JSON request", e); }
    }

    private void methodNotAllowed(HttpExchange exchange, String allow) throws IOException {
        exchange.getResponseHeaders().set("Allow", allow);
        respondError(exchange, 405, "method not allowed");
    }

    private void respondJson(HttpExchange exchange, int status, Object value) throws IOException {
        var bytes = json.writeValueAsBytes(value);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var output = exchange.getResponseBody()) { output.write(bytes); }
    }

    private void respondError(HttpExchange exchange, int status, String message) throws IOException {
        respondJson(exchange, status, Map.of("error", message == null ? "request failed" : message));
    }

    private void respondEmpty(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
        exchange.close();
    }

    private void uncheckedJson(HttpExchange exchange, int status, Object value) {
        try { respondJson(exchange, status, value); } catch (IOException ignored) { exchange.close(); }
    }

    private void uncheckedError(HttpExchange exchange, int status, String message) {
        try { respondError(exchange, status, message); } catch (IOException ignored) { exchange.close(); }
    }

    public record CreateRequest(String id, String student, String course, int amountInCents) {}
    public record UpdateRequest(String student, String course) {}

    @Override public void close() { server.stop(1); }
}
