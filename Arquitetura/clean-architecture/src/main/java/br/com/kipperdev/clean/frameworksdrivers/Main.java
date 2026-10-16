package br.com.kipperdev.clean.frameworksdrivers;

import br.com.kipperdev.clean.interfaceadapters.controllers.AppMaxWebhookController;
import br.com.kipperdev.clean.interfaceadapters.controllers.ConsoleEnrollmentController;
import br.com.kipperdev.clean.interfaceadapters.controllers.EnrollmentHttpController;
import br.com.kipperdev.clean.interfaceadapters.gateways.AppMaxApiSimulator;
import br.com.kipperdev.clean.interfaceadapters.gateways.AppMaxConfiguration;
import br.com.kipperdev.clean.interfaceadapters.gateways.AppMaxHttpPaymentAdapter;
import br.com.kipperdev.clean.interfaceadapters.gateways.AppMaxPaymentAdapter;
import br.com.kipperdev.clean.interfaceadapters.gateways.JpaEnrollmentRepository;
import br.com.kipperdev.clean.usecases.ConfirmEnrollmentPayment;
import br.com.kipperdev.clean.usecases.EnrollStudent;
import br.com.kipperdev.clean.usecases.ManageEnrollments;
import br.com.kipperdev.clean.usecases.PaymentProvider;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/** Composition root: selects concrete gateways and starts the requested driver. */
public final class Main {
    private static final Map<String, String> DEMO_STATUSES = Map.of(
            "enr-ana", "paid", "enr-bia", "pending", "enr-clara", "refused");

    private Main() {}

    public static void main(String[] args) throws Exception {
        var env = System.getenv();
        var jdbcUrl = env.getOrDefault("DATABASE_URL",
                "jdbc:h2:file:./data/enrollments;DB_CLOSE_ON_EXIT=FALSE");
        if (!jdbcUrl.startsWith("jdbc:h2:file:"))
            throw new IllegalArgumentException("DATABASE_URL must point to a file-backed H2 database");
        var databasePath = jdbcUrl.substring("jdbc:h2:file:".length()).split(";", 2)[0];
        var databaseFile = java.nio.file.Path.of(databasePath);
        if (databaseFile.getParent() != null) java.nio.file.Files.createDirectories(databaseFile.getParent());

        var mode = args.length == 0 ? "api" : args[0];
        try (var repository = new JpaEnrollmentRepository(jdbcUrl)) {
            switch (mode) {
                case "api" -> runApi(repository, env);
                case "demo" -> runDemo(repository);
                default -> {
                    System.out.println("Usage: ./executar.sh [api | demo]");
                    System.exit(2);
                }
            }
        }
    }

    private static void runDemo(JpaEnrollmentRepository repository) {
        var payment = new AppMaxPaymentAdapter(new AppMaxApiSimulator(DEMO_STATUSES));
        var enroll = new EnrollStudent(payment, repository, repository);
        var console = new ConsoleEnrollmentController(enroll);
        console.enroll("enr-ana", "Ana", "Arquitetura", 10_000);
        console.enroll("enr-bia", "Bia", "Arquitetura", 10_000);
        console.enroll("enr-clara", "Clara", "Arquitetura", 10_000);
        System.out.println("Matrículas no H2: " + repository.findAll().stream().map(e -> e.student()).toList());
    }

    private static void runApi(JpaEnrollmentRepository repository, Map<String, String> env) throws Exception {
        var mode = env.getOrDefault("APPMAX_MODE", "simulation");
        final PaymentProvider payment;
        final PaymentProvider.CustomerProfile customer;
        final boolean realAppMax;
        if ("http".equalsIgnoreCase(mode)) {
            var config = AppMaxConfiguration.from(env);
            customer = config.customerProfile(env);
            payment = new AppMaxHttpPaymentAdapter(config);
            realAppMax = true;
        } else if ("simulation".equalsIgnoreCase(mode)) {
            customer = null;
            payment = new AppMaxPaymentAdapter(new AppMaxApiSimulator(Map.of(
                    "enr-bia", "pending", "enr-clara", "refused")));
            realAppMax = false;
        } else {
            throw new IllegalArgumentException("APPMAX_MODE must be simulation or http");
        }

        var enroll = new EnrollStudent(payment, repository, repository);
        var manage = new ManageEnrollments(repository);
        int apiPort = Integer.parseInt(env.getOrDefault("API_PORT", "8080"));
        try (var api = new EnrollmentHttpController(apiPort, enroll, manage, customer)) {
            AppMaxWebhookController webhook = null;
            try {
                if (realAppMax) {
                    int webhookPort = Integer.parseInt(env.getOrDefault("APPMAX_WEBHOOK_PORT", "8081"));
                    webhook = new AppMaxWebhookController(webhookPort, repository,
                            new ConfirmEnrollmentPayment(payment, repository));
                    webhook.start();
                }
                api.start();
                System.out.println("Enrollment API listening at http://127.0.0.1:" + api.port() + "/api/enrollments");
                if (webhook != null)
                    System.out.println("AppMax webhook listening at http://127.0.0.1:" + webhook.port() + "/webhooks/appmax");
                new CountDownLatch(1).await();
            } finally {
                if (webhook != null) webhook.close();
            }
        }
    }
}
