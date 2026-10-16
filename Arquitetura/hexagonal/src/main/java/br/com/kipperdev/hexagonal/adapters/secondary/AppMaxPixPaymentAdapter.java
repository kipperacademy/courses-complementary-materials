package br.com.kipperdev.hexagonal.adapters.secondary;

import br.com.kipperdev.hexagonal.application.ports.secondary.PixPaymentProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;

/** Real AppMax adapter, deliberately limited to Pix. It never accepts card data or tokens. */
public final class AppMaxPixPaymentAdapter implements PixPaymentProvider {
    private static final Set<String> APP_MAX_ORDER_STATUSES = Set.of(
            "pendente", "aprovado", "autorizado", "cancelado", "estornado", "recusado_por_risco",
            "integrado", "pendente_integracao", "pendente_integracao_em_analise", "chargeback_em_tratativa",
            "chargeback_em_disputa", "chargeback_perdido", "chargeback_vencido");
    private final URI apiBase;
    private final URI authBase;
    private final String clientId;
    private final String clientSecret;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    private final ObjectMapper json = new ObjectMapper();

    public AppMaxPixPaymentAdapter(URI apiBase, URI authBase, String clientId, String clientSecret) {
        this.apiBase = requireHttpsBase(apiBase, "API base URL");
        this.authBase = requireHttpsBase(authBase, "auth base URL");
        this.clientId = required(clientId, "APP_MAX_CLIENT_ID");
        this.clientSecret = required(clientSecret, "APP_MAX_CLIENT_SECRET");
    }

    public static AppMaxPixPaymentAdapter fromEnvironment() {
        var apiUrl = System.getenv().getOrDefault("APP_MAX_API_BASE_URL", "https://api.sandboxappmax.com.br");
        var api = URI.create(apiUrl);
        var defaultAuth = api.getHost() != null && api.getHost().startsWith("api.sandbox")
                ? "https://auth.sandboxappmax.com.br" : "https://auth.appmax.com.br";
        var authUrl = System.getenv().getOrDefault("APP_MAX_AUTH_BASE_URL", defaultAuth);
        return new AppMaxPixPaymentAdapter(URI.create(apiUrl), URI.create(authUrl),
                System.getenv("APP_MAX_CLIENT_ID"), System.getenv("APP_MAX_CLIENT_SECRET"));
    }

    @Override public PixCharge create(PixCommand c) {
        required(c.enrollmentId(), "enrollment id"); required(c.firstName(), "first name");
        required(c.lastName(), "last name"); required(c.email(), "email"); required(c.phone(), "phone");
        required(c.ip(), "IP collected by the checkout"); required(c.documentNumber(), "CPF/CNPJ");
        required(c.student(), "student"); required(c.course(), "course");
        if (c.amountInCents() <= 0) throw new IllegalArgumentException("Amount must be positive cents");

        var token = token();
        var customerBody = json.createObjectNode().put("first_name", c.firstName()).put("last_name", c.lastName())
                .put("email", c.email()).put("phone", c.phone()).put("ip", c.ip()).put("document_number", c.documentNumber());
        var customerResponse = post("/v1/customers", customerBody, token);
        var customerId = requiredText(customerResponse.at("/data/customer/id"), "customer id");

        var product = json.createObjectNode().put("sku", c.course()).put("name", c.course()).put("quantity", 1)
                .put("unit_value", c.amountInCents()).put("type", "digital");
        var orderBody = json.createObjectNode().put("customer_id", parseOrderId(customerId));
        orderBody.putArray("products").add(product);
        var orderResponse = post("/v1/orders", orderBody, token);
        var orderId = requiredText(orderResponse.at("/data/order/id"), "order id");
        validateOrderStatusIfPresent(orderResponse.at("/data/order/status"));

        var pixBody = json.createObjectNode().put("order_id", parseOrderId(orderId));
        pixBody.putObject("payment_data").putObject("pix").put("document_number", c.documentNumber());
        var pixResponse = post("/v1/payments/pix", pixBody, token);
        var data = pixResponse.path("data");
        var returnedOrder = data.path("order");
        var returnedOrderId = optionalText(returnedOrder.get("id"), "");
        var payment = data.path("payment");
        var paymentOrderId = optionalText(payment.get("order_id"), "");
        if (!returnedOrderId.isBlank() && !orderId.equals(returnedOrderId))
            throw new IllegalStateException("AppMax Pix response order id does not match the created order");
        if (!paymentOrderId.isBlank() && !orderId.equals(paymentOrderId))
            throw new IllegalStateException("AppMax Pix payment order id does not match the created order");
        var status = optionalText(returnedOrder.get("status"), "pendente");
        validateOrderStatusIfPresent(returnedOrder.get("status"));

        // AppMax documentation currently publishes both /data/pix/* and /data/payment/pix_* response shapes.
        var pix = data.path("pix");
        var qrCode = firstText(pix.get("qr_code"), payment.get("pix_qrcode"));
        var emvCode = firstText(pix.get("emv_code"), payment.get("pix_emv"));
        var expiresAt = firstText(pix.get("expires_at"), payment.get("pix_expiration_date"));
        if (qrCode.isBlank() || emvCode.isBlank())
            throw new IllegalStateException("AppMax Pix response did not include QR code and EMV instructions");
        return new PixCharge(orderId, status, qrCode, emvCode, expiresAt);
    }

    @Override public OrderPaymentState getOrderPaymentState(String appMaxOrderId) {
        var id = parseOrderId(appMaxOrderId);
        var response = get("/v1/orders/" + id, token());
        var order = response.at("/data/order");
        var returnedId = optionalText(order.get("id"), "");
        if (!returnedId.isBlank() && !returnedId.equals(Long.toString(id)))
            throw new IllegalStateException("AppMax GET response order id does not match the requested order");
        var status = requiredText(order.get("status"), "order status");
        validateOrderStatusIfPresent(order.get("status"));
        return new OrderPaymentState(status, optionalLong(order.get("total_paid")));
    }

    private String token() {
        var form = "grant_type=client_credentials&client_id=" + form(clientId) + "&client_secret=" + form(clientSecret);
        var request = HttpRequest.newBuilder(authBase.resolve("/oauth2/token"))
                .timeout(Duration.ofSeconds(30)).header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form)).build();
        var response = send(request);
        return requiredText(response.get("access_token"), "access token");
    }

    private JsonNode post(String path, JsonNode body, String token) {
        var request = HttpRequest.newBuilder(apiBase.resolve(path)).timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + token).header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString())).build();
        return send(request);
    }

    private JsonNode get(String path, String token) {
        var request = HttpRequest.newBuilder(apiBase.resolve(path)).timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + token).header("Accept", "application/json").GET().build();
        return send(request);
    }

    private JsonNode send(HttpRequest request) {
        try {
            var response = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("AppMax request failed with HTTP " + response.statusCode());
            }
            return json.readTree(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("AppMax request interrupted", e);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not communicate with AppMax", e);
        }
    }

    private static URI requireHttpsBase(URI uri, String label) {
        if (uri == null || !"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null ||
                uri.getRawPath() != null && !uri.getRawPath().isEmpty() && !uri.getRawPath().equals("/"))
            throw new IllegalArgumentException(label + " must be an HTTPS origin without a path");
        return uri;
    }
    private static String required(String value, String label) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " is required");
        return value;
    }
    private static String requiredText(JsonNode node, String label) {
        if (node == null || node.isMissingNode() || node.isNull() || node.asText().isBlank())
            throw new IllegalStateException("AppMax response did not include " + label);
        return node.asText();
    }
    private static String optionalText(JsonNode node, String fallback) {
        return node == null || node.isMissingNode() || node.isNull() ? fallback : node.asText(fallback);
    }
    private static String firstText(JsonNode primary, JsonNode fallback) {
        var first = optionalText(primary, "");
        return first.isBlank() ? optionalText(fallback, "") : first;
    }
    private static void validateOrderStatusIfPresent(JsonNode statusNode) {
        if (statusNode == null || statusNode.isMissingNode() || statusNode.isNull()) return;
        var status = statusNode.asText("").toLowerCase(java.util.Locale.ROOT);
        if (!APP_MAX_ORDER_STATUSES.contains(status))
            throw new IllegalStateException("AppMax response included an unrecognized order status");
    }
    private static Long optionalLong(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        if (node.isIntegralNumber() && node.canConvertToLong()) return node.longValue();
        if (node.isTextual()) {
            try { return Long.parseLong(node.asText()); }
            catch (NumberFormatException e) { throw new IllegalStateException("AppMax total_paid is not an integer amount in cents", e); }
        }
        throw new IllegalStateException("AppMax total_paid is not an integer amount in cents");
    }
    private static long parseOrderId(String id) {
        try { return Long.parseLong(id); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("AppMax order id must be numeric", e); }
    }
    private static String form(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
}
