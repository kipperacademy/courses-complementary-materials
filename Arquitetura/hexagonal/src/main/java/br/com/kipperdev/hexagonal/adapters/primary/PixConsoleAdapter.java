package br.com.kipperdev.hexagonal.adapters.primary;

import br.com.kipperdev.hexagonal.application.ports.primary.CheckPixEnrollment;
import br.com.kipperdev.hexagonal.application.ports.primary.CreatePixEnrollment;

import java.util.Scanner;

/** Translates Pix console commands into calls to the application's primary ports. */
public final class PixConsoleAdapter {
    private final CreatePixEnrollment create;
    private final CheckPixEnrollment check;

    public PixConsoleAdapter(CreatePixEnrollment create, CheckPixEnrollment check) {
        this.create = create;
        this.check = check;
    }

    public void createPix() {
        var input = new Scanner(System.in);
        System.out.println("Criação de Pix AppMax. Dados de contato/IP devem vir do checkout e ser tratados conforme sua política de privacidade.");
        var enrollmentId = ask(input, "ID interno da matrícula");
        var firstName = ask(input, "Nome");
        var lastName = ask(input, "Sobrenome");
        var email = ask(input, "Email");
        var phone = ask(input, "Telefone");
        var ip = ask(input, "IP coletado pelo checkout/Appmax JS");
        var documentNumber = ask(input, "CPF/CNPJ do pagador");
        var student = ask(input, "Nome do estudante");
        var course = ask(input, "Curso");
        int cents;
        try {
            cents = Integer.parseInt(ask(input, "Valor em centavos"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount must be an integer number of cents");
        }

        var pending = create.create(new CreatePixEnrollment.Command(enrollmentId, firstName, lastName, email,
                phone, ip, documentNumber, student, course, cents));
        System.out.printf("Pix criado e salvo como pendente. matrícula=%s | AppMax order=%s | status=%s | expira=%s%n",
                pending.enrollmentId(), pending.appMaxOrderId(), pending.providerStatus(), pending.expiresAt());
        System.out.println("EMV para pagamento: " + pending.emvCode());
        System.out.println("QR code retornado: " + pending.qrCode());
        System.out.println("Após pagar, verifique com: ./executar.sh pix-check " + pending.enrollmentId());
        System.out.println("Matrícula não será liberada enquanto a consulta autenticada não retornar aprovado/integrado.");
    }

    public void checkPix(String enrollmentId) {
        var result = check.check(enrollmentId);
        System.out.printf("AppMax order status=%s | total_paid=%s | payment=%s | enrollment=%s%n",
                result.providerStatus(), result.totalPaidInCents() == null ? "missing" : result.totalPaidInCents(),
                result.paymentStatus(), result.enrollment().isPresent() ? "confirmed" : "pending/not confirmed");
    }

    private static String ask(Scanner input, String prompt) {
        System.out.print(prompt + ": ");
        var value = input.nextLine().trim();
        if (value.isEmpty()) throw new IllegalArgumentException(prompt + " is required");
        return value;
    }
}
