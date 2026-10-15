package br.com.kipperdev.hexagonal.adapters.primary;

import br.com.kipperdev.hexagonal.application.ports.primary.EnrollStudent;

/** Adaptador de entrada: traduz dados da demonstração e aciona a porta da aplicação. */
public final class ConsoleEnrollmentAdapter {
    private final EnrollStudent enrollStudent;

    public ConsoleEnrollmentAdapter(EnrollStudent enrollStudent) { this.enrollStudent = enrollStudent; }

    public void enroll(String id, String student, String course, int cents) {
        var result = enrollStudent.enroll(new EnrollStudent.Command(id, student, course, cents));
        System.out.printf("%s | pagamento=%s | matrícula=%s | referência=%s%n",
                student, result.paymentStatus(), result.enrollment().isPresent() ? "liberada" : "não liberada",
                result.paymentReference());
    }
}
