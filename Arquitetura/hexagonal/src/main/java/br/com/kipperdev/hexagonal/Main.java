package br.com.kipperdev.hexagonal;

import br.com.kipperdev.hexagonal.adapters.primary.ConsoleEnrollmentAdapter;
import br.com.kipperdev.hexagonal.adapters.primary.EnrollmentHttpAdapter;
import br.com.kipperdev.hexagonal.adapters.primary.PixConsoleAdapter;
import br.com.kipperdev.hexagonal.adapters.secondary.AppMaxApiSimulator;
import br.com.kipperdev.hexagonal.adapters.secondary.AppMaxPaymentAdapter;
import br.com.kipperdev.hexagonal.adapters.secondary.AppMaxPixPaymentAdapter;
import br.com.kipperdev.hexagonal.adapters.secondary.JpaEnrollmentRepository;
import br.com.kipperdev.hexagonal.application.usecases.CheckPixEnrollmentService;
import br.com.kipperdev.hexagonal.application.usecases.CreatePixEnrollmentService;
import br.com.kipperdev.hexagonal.application.usecases.EnrollStudentService;
import br.com.kipperdev.hexagonal.application.usecases.ManageEnrollmentsService;

import java.util.concurrent.CountDownLatch;

/** Composition root. Simulated payment remains the default mode. */
public final class Main {
    private Main() {}

    public static void main(String[] args) throws Exception {
        var databaseUrl = System.getenv().getOrDefault("ENROLLMENT_DB_URL",
                "jdbc:h2:file:./data/enrollments;DB_CLOSE_ON_EXIT=FALSE");
        try (var repository = new JpaEnrollmentRepository(databaseUrl)) {
            var mode = args.length == 0 ? "api" : args[0];
            switch (mode) {
                case "api" -> runApi(repository);
                case "demo" -> runDemo(repository);
                case "pix-create" -> runPixCreate(repository);
                case "pix-check" -> {
                    if (args.length != 2) usage();
                    runPixCheck(repository, args[1]);
                }
                default -> usage();
            }
        }
    }

    private static void runApi(JpaEnrollmentRepository repository) throws Exception {
        var simulator = new AppMaxApiSimulator(java.util.Map.of(
                "enr-bia", "pending", "enr-clara", "refused"));
        var payment = new AppMaxPaymentAdapter(simulator);
        var enroll = new EnrollStudentService(payment, repository);
        var manage = new ManageEnrollmentsService(repository);
        var port = Integer.parseInt(System.getenv().getOrDefault("API_PORT", "8080"));
        try (var api = new EnrollmentHttpAdapter(port, enroll, manage)) {
            api.start();
            System.out.println("Enrollment API listening at http://127.0.0.1:" + api.port() + "/api/enrollments");
            new CountDownLatch(1).await();
        }
    }

    private static void runDemo(JpaEnrollmentRepository repository) {
        var appMaxSimulator = new AppMaxApiSimulator(java.util.Map.of(
                "enr-ana", "paid", "enr-bia", "pending", "enr-clara", "refused"));
        var paymentAdapter = new AppMaxPaymentAdapter(appMaxSimulator);
        var useCase = new EnrollStudentService(paymentAdapter, repository);
        var console = new ConsoleEnrollmentAdapter(useCase);
        console.enroll("enr-ana", "Ana", "Arquitetura", 10_000);
        console.enroll("enr-bia", "Bia", "Arquitetura", 10_000);
        console.enroll("enr-clara", "Clara", "Arquitetura", 10_000);
        System.out.println("Matrículas no H2: " + repository.findAll().stream().map(e -> e.student()).toList());
    }

    private static void runPixCreate(JpaEnrollmentRepository repository) {
        var provider = AppMaxPixPaymentAdapter.fromEnvironment();
        var create = new CreatePixEnrollmentService(provider, repository);
        var check = new CheckPixEnrollmentService(provider, repository);
        new PixConsoleAdapter(create, check).createPix();
    }

    private static void runPixCheck(JpaEnrollmentRepository repository, String enrollmentId) {
        var provider = AppMaxPixPaymentAdapter.fromEnvironment();
        var create = new CreatePixEnrollmentService(provider, repository);
        var check = new CheckPixEnrollmentService(provider, repository);
        new PixConsoleAdapter(create, check).checkPix(enrollmentId);
    }

    private static void usage() {
        System.out.println("Usage: ./executar.sh [api | demo | pix-create | pix-check <enrollment-id>]");
        System.out.println("Real Pix mode requires APP_MAX_CLIENT_ID and APP_MAX_CLIENT_SECRET; sandbox API is the default.");
        System.exit(2);
    }
}
