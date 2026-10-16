package br.com.kipperdev.hexagonal.domain;

/** Persisted provider order and Pix instructions until an authenticated status check confirms payment. */
public record PendingPixPayment(
        String enrollmentId,
        String student,
        String course,
        int amountInCents,
        String appMaxOrderId,
        String providerStatus,
        String qrCode,
        String emvCode,
        String expiresAt) {
    public PendingPixPayment {
        if (enrollmentId == null || enrollmentId.isBlank()) throw new IllegalArgumentException("Enrollment id required");
        if (student == null || student.isBlank()) throw new IllegalArgumentException("Student required");
        if (course == null || course.isBlank()) throw new IllegalArgumentException("Course required");
        if (amountInCents <= 0) throw new IllegalArgumentException("Amount must be positive cents");
        if (appMaxOrderId == null || appMaxOrderId.isBlank()) throw new IllegalArgumentException("AppMax order id required");
        if (providerStatus == null || providerStatus.isBlank()) throw new IllegalArgumentException("Provider status required");
    }
}
