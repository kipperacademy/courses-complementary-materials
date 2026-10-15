package br.com.kipperdev.hexagonal.adapters.secondary;

import br.com.kipperdev.hexagonal.application.ports.secondary.EnrollmentRepository;
import br.com.kipperdev.hexagonal.domain.Enrollment;
import br.com.kipperdev.hexagonal.domain.PendingPixPayment;
import java.util.ArrayList;
import java.util.List;

public final class InMemoryEnrollmentRepository implements EnrollmentRepository {
    private final List<Enrollment> enrollments = new ArrayList<>();
    private final java.util.Map<String, PendingPixPayment> pendingPix = new java.util.HashMap<>();

    @Override public void save(Enrollment enrollment) {
        enrollments.removeIf(existing -> existing.id().equals(enrollment.id()));
        enrollments.add(enrollment);
    }
    @Override public List<Enrollment> findAll() { return List.copyOf(enrollments); }
    @Override public java.util.Optional<Enrollment> findById(String id) {
        return enrollments.stream().filter(enrollment -> enrollment.id().equals(id)).findFirst();
    }
    @Override public void deleteById(String id) {
        enrollments.removeIf(enrollment -> enrollment.id().equals(id));
        pendingPix.remove(id);
    }
    @Override public void savePendingPix(PendingPixPayment payment) { pendingPix.put(payment.enrollmentId(), payment); }
    @Override public java.util.Optional<PendingPixPayment> findPendingPix(String id) { return java.util.Optional.ofNullable(pendingPix.get(id)); }
    @Override public void updatePendingPixStatus(String id, String status) {
        var old = pendingPix.get(id);
        if (old == null) throw new IllegalArgumentException("Pending Pix payment not found: " + id);
        pendingPix.put(id, new PendingPixPayment(old.enrollmentId(), old.student(), old.course(), old.amountInCents(),
                old.appMaxOrderId(), status, old.qrCode(), old.emvCode(), old.expiresAt()));
    }
    @Override public void confirmPixEnrollment(String id, String status) {
        var old = pendingPix.get(id);
        if (old == null) throw new IllegalArgumentException("Pending Pix payment not found: " + id);
        updatePendingPixStatus(id, status);
        save(new Enrollment(old.enrollmentId(), old.student(), old.course(), old.amountInCents()));
    }
}
