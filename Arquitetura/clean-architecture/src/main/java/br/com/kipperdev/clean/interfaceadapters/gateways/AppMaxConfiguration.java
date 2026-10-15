package br.com.kipperdev.clean.interfaceadapters.gateways;

import br.com.kipperdev.clean.usecases.PaymentProvider;

import java.net.URI;
import java.util.Map;

/** Environment-backed credentials and API endpoints; never logs or stores secrets in source. */
public record AppMaxConfiguration(URI authBaseUrl, URI apiBaseUrl, String merchantClientId,
                                  String merchantClientSecret) {
    public static AppMaxConfiguration from(Map<String, String> env) {
        var environment = env.getOrDefault("APPMAX_ENV", "sandbox").toLowerCase();
        if (!environment.equals("sandbox") && !environment.equals("production")) {
            throw new IllegalArgumentException("APPMAX_ENV must be sandbox or production");
        }
        var defaultAuth = environment.equals("sandbox")
                ? "https://auth.sandboxappmax.com.br" : "https://auth.appmax.com.br";
        var defaultApi = environment.equals("sandbox")
                ? "https://api.sandboxappmax.com.br" : "https://api.appmax.com.br";
        var auth = URI.create(env.getOrDefault("APPMAX_AUTH_BASE_URL", defaultAuth));
        var api = URI.create(env.getOrDefault("APPMAX_API_BASE_URL", defaultApi));
        requireHttps(auth, "APPMAX_AUTH_BASE_URL");
        requireHttps(api, "APPMAX_API_BASE_URL");
        return new AppMaxConfiguration(auth, api, required(env, "APPMAX_MERCHANT_CLIENT_ID"),
                required(env, "APPMAX_MERCHANT_CLIENT_SECRET"));
    }

    public PaymentProvider.CustomerProfile customerProfile(Map<String, String> env) {
        return new PaymentProvider.CustomerProfile(required(env, "APPMAX_CUSTOMER_FIRST_NAME"),
                required(env, "APPMAX_CUSTOMER_LAST_NAME"), required(env, "APPMAX_CUSTOMER_EMAIL"),
                required(env, "APPMAX_CUSTOMER_PHONE"), required(env, "APPMAX_CUSTOMER_IP"),
                required(env, "APPMAX_CUSTOMER_DOCUMENT_NUMBER"));
    }

    private static void requireHttps(URI uri, String setting) {
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
            throw new IllegalArgumentException(setting + " must be an HTTPS origin");
        }
    }

    private static String required(Map<String, String> env, String key) {
        var value = env.get(key);
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Missing environment variable " + key);
        return value.trim();
    }
}
