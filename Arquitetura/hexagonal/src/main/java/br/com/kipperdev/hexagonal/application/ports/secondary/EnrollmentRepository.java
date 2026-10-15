package br.com.kipperdev.hexagonal.application.ports.secondary;

import br.com.kipperdev.hexagonal.domain.Enrollment;
import br.com.kipperdev.hexagonal.domain.PendingPixPayment;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    List<Enrollment> findAll();
    Optional<Enrollment> findById(String id);
    void deleteById(String id);
    void savePendingPix(PendingPixPayment payment);
    java.util.Optional<PendingPixPayment> findPendingPix(String enrollmentId);
    void updatePendingPixStatus(String enrollmentId, String providerStatus);
    void confirmPixEnrollment(String enrollmentId, String providerStatus);
}
