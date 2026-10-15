package br.com.kipperdev.hexagonal.adapters.secondary.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "pending_pix_payments", uniqueConstraints =
        @UniqueConstraint(name = "uk_pending_pix_appmax_order", columnNames = "appmax_order_id"))
public class PendingPixPaymentEntity {
    @Id
    @Column(name = "enrollment_id", nullable = false, length = 120)
    private String enrollmentId;
    @Column(nullable = false, length = 200)
    private String student;
    @Column(nullable = false, length = 200)
    private String course;
    @Column(name = "amount_in_cents", nullable = false)
    private int amountInCents;
    @Column(name = "appmax_order_id", nullable = false, unique = true, length = 80)
    private String appMaxOrderId;
    @Column(name = "provider_status", nullable = false, length = 80)
    private String providerStatus;
    @Lob
    @Column(name = "qr_code")
    private String qrCode;
    @Lob
    @Column(name = "emv_code")
    private String emvCode;
    @Column(name = "expires_at", length = 80)
    private String expiresAt;

    protected PendingPixPaymentEntity() {}
    public PendingPixPaymentEntity(String enrollmentId, String student, String course, int amountInCents,
                                   String appMaxOrderId, String providerStatus, String qrCode,
                                   String emvCode, String expiresAt) {
        this.enrollmentId = enrollmentId; this.student = student; this.course = course;
        this.amountInCents = amountInCents; this.appMaxOrderId = appMaxOrderId;
        this.providerStatus = providerStatus; this.qrCode = qrCode; this.emvCode = emvCode;
        this.expiresAt = expiresAt;
    }
    public String getEnrollmentId() { return enrollmentId; }
    public String getStudent() { return student; }
    public String getCourse() { return course; }
    public int getAmountInCents() { return amountInCents; }
    public String getAppMaxOrderId() { return appMaxOrderId; }
    public String getProviderStatus() { return providerStatus; }
    public String getQrCode() { return qrCode; }
    public String getEmvCode() { return emvCode; }
    public String getExpiresAt() { return expiresAt; }
    public void updateStatus(String providerStatus) { this.providerStatus = providerStatus; }
}
