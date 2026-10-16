package br.com.kipperdev.clean.interfaceadapters.gateways.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "confirmed_payment_references")
public class ConfirmedPaymentEntity {
    @Id @Column(name = "payment_reference", nullable = false, length = 120) private String paymentReference;
    @Column(name = "enrollment_id", nullable = false, length = 120) private String enrollmentId;
    @Column(name = "confirmed_at", nullable = false) private LocalDateTime confirmedAt;

    protected ConfirmedPaymentEntity() {}
    public ConfirmedPaymentEntity(String paymentReference, String enrollmentId) {
        this.paymentReference = paymentReference; this.enrollmentId = enrollmentId;
        this.confirmedAt = LocalDateTime.now();
    }
    public String getPaymentReference() { return paymentReference; }
}
