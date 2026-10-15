package br.com.kipperdev.clean.interfaceadapters.gateways;

import br.com.kipperdev.clean.usecases.PaymentProvider;
import br.com.kipperdev.clean.entities.PaymentStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

/** AppMax REST adapter. Supports PIX only; card data is never accepted by this backend. */
public final class AppMaxHttpPaymentAdapter implements PaymentProvider {
    private final AppMaxConfiguration config;
    private final HttpClient http;
    private final ObjectMapper json = new ObjectMapper();
    private String accessToken;
    private Instant tokenExpiresAt = Instant.EPOCH;

    public AppMaxHttpPaymentAdapter(AppMaxConfiguration config) {
        this(config, HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
    }

    AppMaxHttpPaymentAdapter(AppMaxConfiguration config, HttpClient http) {
        this.config = config;
        this.http = http;
    }

    @Override public PaymentResult charge(PaymentCommand command) {
        var customer = requireCustomer(command.customer());
        var token = accessToken();
        var customerId = createCustomer(token, customer);
        var orderId = createOrder(token, customerId, command);
        var pix = createPix(token, orderId, customer.documentNumber());
        // PIX creation is not payment confirmation. Access remains blocked until webhook-triggered GET.
        return new PaymentResult(PaymentStatus.PENDING, orderId, pix.emv(), pix.qrCode(), pix.expiration());
    }

    @Override public PaymentStatus verify(String paymentReference, int expectedAmountInCents) {
        if (paymentReference == null || !paymentReference.matches("[0-9]+")) {
            throw new IllegalArgumentException("AppMax order reference must be numeric");
        }
        var response = get("/v1/orders/" + paymentReference, accessToken());
        var order = requiredNode(response, "data", "order");
        if (!paymentReference.equals(order.path("id").asText())) {
            throw new AppMaxApiException("AppMax returned an order different from the requested order");
        }
        var status = toInternalStatus(order.path("status").asText());
        if (status == PaymentStatus.CONFIRMED && order.path("total_paid").asLong(-1) != expectedAmountInCents) {
            return PaymentStatus.PENDING;
        }
        return status;
    }

    private PaymentProvider.CustomerProfile requireCustomer(CustomerProfile customer) {
        if (customer == null) throw new IllegalArgumentException("Customer profile is required for AppMax PIX");
        for (var value : new String[]{customer.firstName(), customer.lastName(), customer.email(), customer.phone(),
                customer.ip(), customer.documentNumber()}) {
            if (value == null || value.isBlank()) throw new IllegalArgumentException("AppMax customer fields must be complete");
        }
        return customer;
    }

    private synchronized String accessToken() {
        if (accessToken != null && Instant.now().isBefore(tokenExpiresAt.minusSeconds(30))) return accessToken;
        try {
            var form = "grant_type=client_credentials&client_id=" + formEncode(config.merchantClientId())
                    + "&client_secret=" + formEncode(config.merchantClientSecret());
            var request = HttpRequest.newBuilder(config.authBaseUrl().resolve("/oauth2/token"))
                    .timeout(Duration.ofSeconds(20)).header("Accept", "application/json")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form)).build();
            var response = http.send(request, HttpResponse.BodyHandlers.ofString());
            var body = successfulJson(response, "OAuth token");
            accessToken = requiredNode(body, "access_token").asText();
            tokenExpiresAt = Instant.now().plusSeconds(Math.max(60, body.path("expires_in").asLong(3600)));
            return accessToken;
        } catch (IOException e) {
            throw new AppMaxApiException("Could not obtain AppMax access token", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppMaxApiException("Interrupted while obtaining AppMax access token", e);
        }
    }

    private String createCustomer(String token, CustomerProfile customer) {
        var body = json.createObjectNode().put("first_name", customer.firstName()).put("last_name", customer.lastName())
                .put("email", customer.email()).put("phone", customer.phone()).put("ip", customer.ip());
        var response = post("/v1/customers", token, body);
        return requiredNode(response, "data", "customer", "id").asText();
    }

    private String createOrder(String token, String customerId, PaymentCommand command) {
        var product = json.createObjectNode().put("sku", command.course()).put("name", command.course())
                .put("quantity", 1).put("unit_value", command.amountInCents()).put("type", "digital");
        var products = json.createArrayNode().add(product);
        var body = json.createObjectNode().put("customer_id", parseNumericId(customerId)).set("products", products);
        var response = post("/v1/orders", token, body);
        return requiredNode(response, "data", "order", "id").asText();
    }

    private PixInstructions createPix(String token, String orderId, String documentNumber) {
        var pixRequest = json.createObjectNode().put("document_number", documentNumber);
        var paymentData = json.createObjectNode().set("pix", pixRequest);
        var body = json.createObjectNode().put("order_id", parseNumericId(orderId)).set("payment_data", paymentData);
        var response = post("/v1/payments/pix", token, body);
        var payment = response.path("data").path("payment");
        var pix = response.path("data").path("pix");
        if (payment.isMissingNode() && pix.isMissingNode())
            throw new AppMaxApiException("AppMax response is missing data.payment or data.pix");
        var emv = firstText(payment, "pix_emv", pix, "emv_code");
        var qr = firstText(payment, "pix_qrcode", pix, "qr_code");
        var expiration = firstText(payment, "pix_expiration_date", pix, "expires_at");
        if (emv == null || qr == null || expiration == null)
            throw new AppMaxApiException("AppMax PIX response is missing its QR code, EMV code or expiration");
        return new PixInstructions(emv, qr, expiration);
    }

    private JsonNode post(String path, String token, JsonNode body) {
        return send(HttpRequest.newBuilder(config.apiBaseUrl().resolve(path)).timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + token).header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString())).build(), path);
    }

    private JsonNode get(String path, String token) {
        return send(HttpRequest.newBuilder(config.apiBaseUrl().resolve(path)).timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + token).header("Accept", "application/json").GET().build(), path);
    }

    private JsonNode send(HttpRequest request, String operation) {
        try {
            return successfulJson(http.send(request, HttpResponse.BodyHandlers.ofString()), operation);
        } catch (IOException e) {
            throw new AppMaxApiException("AppMax request failed: " + operation, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppMaxApiException("Interrupted AppMax request: " + operation, e);
        }
    }

    private JsonNode successfulJson(HttpResponse<String> response, String operation) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new AppMaxApiException("AppMax " + operation + " failed with HTTP " + response.statusCode());
        }
        try { return json.readTree(response.body()); }
        catch (IOException e) { throw new AppMaxApiException("Invalid JSON from AppMax " + operation, e); }
    }

    private static JsonNode requiredNode(JsonNode node, String... path) {
        for (var key : path) node = node.path(key);
        if (node.isMissingNode() || node.isNull() || node.isContainerNode() || node.asText().isBlank()) {
            throw new AppMaxApiException("AppMax response is missing " + String.join(".", path));
        }
        return node;
    }

    private static String firstText(JsonNode node1, String key1, JsonNode node2, String key2) {
        var first = node1.path(key1);
        if (!first.isMissingNode() && !first.isNull() && !first.asText().isBlank()) return first.asText();
        var second = node2.path(key2);
        return second.isMissingNode() || second.isNull() || second.asText().isBlank() ? null : second.asText();
    }

    private static long parseNumericId(String value) {
        try { return Long.parseLong(value); }
        catch (NumberFormatException e) { throw new AppMaxApiException("AppMax returned a non-numeric ID", e); }
    }

    private static PaymentStatus toInternalStatus(String status) {
        return switch (status.toLowerCase()) {
            case "aprovado", "integrado" -> PaymentStatus.CONFIRMED;
            case "cancelado", "estornado", "recusado_por_risco" -> PaymentStatus.DECLINED;
            case "pendente", "autorizado", "pendente_integracao", "pendente_integracao_em_analise" -> PaymentStatus.PENDING;
            default -> PaymentStatus.UNKNOWN;
        };
    }

    private static String formEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private record PixInstructions(String emv, String qrCode, String expiration) {}

    public static final class AppMaxApiException extends RuntimeException {
        public AppMaxApiException(String message) { super(message); }
        public AppMaxApiException(String message, Throwable cause) { super(message, cause); }
    }
}
