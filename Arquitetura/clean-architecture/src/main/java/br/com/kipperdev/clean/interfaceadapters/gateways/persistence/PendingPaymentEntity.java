package br.com.kipperdev.clean.interfaceadapters.gateways.persistence;

import br.com.kipperdev.clean.usecases.PendingPaymentRepository;
import br.com.kipperdev.clean.entities.Enrollment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(name = "pending_payments", uniqueConstraints =
        @UniqueConstraint(name = "uk_pending_enrollment", columnNames = "enrollment_id"))
public class PendingPaymentEntity {
    @Id @Column(name = "payment_reference", nullable = false, length = 120) private String paymentReference;
    @Column(name = "enrollment_id", nullable = false, length = 120) private String enrollmentId;
    @Column(nullable = false, length = 200) private String student;
    @Column(nullable = false, length = 200) private String course;
    @Column(name = "amount_in_cents", nullable = false) private int amountInCents;
    @Column(name = "pix_emv", length = 2000) private String pixEmv;
    @Column(name = "pix_qr_code", length = 1000000) private String pixQrCode;
    @Column(name = "pix_expiration", length = 80) private String pixExpiration;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;

    protected PendingPaymentEntity() {}

    public PendingPaymentEntity(PendingPaymentRepository.PendingEnrollment pending) {
        var enrollment = pending.enrollment();
        this.paymentReference = pending.paymentReference(); this.enrollmentId = enrollment.id();
        this.student = enrollment.student(); this.course = enrollment.course();
        this.amountInCents = enrollment.amountInCents(); this.pixEmv = pending.pixEmv();
        this.pixQrCode = pending.pixQrCode(); this.pixExpiration = pending.pixExpiration();
        this.createdAt = LocalDateTime.now();
    }

    public PendingPaymentRepository.PendingEnrollment toDomain() {
        return new PendingPaymentRepository.PendingEnrollment(
                new Enrollment(enrollmentId, student, course, amountInCents), paymentReference,
                pixEmv, pixQrCode, pixExpiration);
    }
    public String getPaymentReference() { return paymentReference; }
    public String getEnrollmentId() { return enrollmentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
