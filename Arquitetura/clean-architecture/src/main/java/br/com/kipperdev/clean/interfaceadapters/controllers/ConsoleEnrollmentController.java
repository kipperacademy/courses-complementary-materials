package br.com.kipperdev.clean.interfaceadapters.controllers;

import br.com.kipperdev.clean.usecases.EnrollStudent;

/** Adapta a entrada da demo para os dados pedidos pelo caso de uso. */
public final class ConsoleEnrollmentController {
    private final EnrollStudent enrollStudent;

    public ConsoleEnrollmentController(EnrollStudent enrollStudent) { this.enrollStudent = enrollStudent; }

    public void enroll(String id, String student, String course, int cents) {
        enroll(id, student, course, cents, null);
    }

    public void enroll(String id, String student, String course, int cents,
                       br.com.kipperdev.clean.usecases.PaymentProvider.CustomerProfile customer) {
        var result = enrollStudent.execute(new EnrollStudent.Request(id, student, course, cents, customer));
        System.out.printf("%s | pagamento=%s | matrícula=%s | referência=%s%n",
                student, result.paymentStatus(), result.enrollment().isPresent() ? "liberada" : "não liberada",
                result.paymentReference());
        if (result.pixEmv() != null) System.out.println("Pix copia-e-cola: " + result.pixEmv());
        if (result.pixQrCode() != null) System.out.println("Pix QR code (PNG base64): " + result.pixQrCode());
        if (result.pixExpiration() != null) System.out.println("Pix expira em: " + result.pixExpiration());
    }
}
