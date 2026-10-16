package br.com.kipperdev.clean.interfaceadapters.gateways.persistence;

import br.com.kipperdev.clean.usecases.PendingPaymentRepository;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(name = "appmax_webhook_inbox", uniqueConstraints =
        @UniqueConstraint(name = "uk_webhook_event_order", columnNames = {"event_name", "order_id"}))
public class WebhookInboxEntity {
    @Id @Column(nullable = false, length = 260) private String id;
    @Column(name = "event_name", nullable = false, length = 80) private String event;
    @Column(name = "order_id", nullable = false, length = 120) private String orderId;
    @Column(name = "raw_payload", nullable = false, length = 100000) private String rawPayload;
    @Column(name = "received_at", nullable = false) private LocalDateTime receivedAt;
    @Column(name = "processed_at") private LocalDateTime processedAt;
    @Column(name = "attempt_count", nullable = false) private int attemptCount;
    @Column(name = "next_attempt_at") private LocalDateTime nextAttemptAt;

    protected WebhookInboxEntity() {}

    public static String key(String event, String orderId) { return event + ":" + orderId; }

    public WebhookInboxEntity(String event, String orderId, String rawPayload) {
        this.id = key(event, orderId); this.event = event; this.orderId = orderId;
        this.rawPayload = rawPayload; this.receivedAt = LocalDateTime.now(); this.attemptCount = 0;
    }

    public PendingPaymentRepository.WebhookEvent toDomain() {
        return new PendingPaymentRepository.WebhookEvent(event, orderId, rawPayload, attemptCount);
    }
    public String getId() { return id; }
    public String getEvent() { return event; }
    public String getOrderId() { return orderId; }
    public String getRawPayload() { return rawPayload; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public int getAttemptCount() { return attemptCount; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }
    public LocalDateTime getNextAttemptAt() { return nextAttemptAt; }
    public void setNextAttemptAt(LocalDateTime nextAttemptAt) { this.nextAttemptAt = nextAttemptAt; }
}
