package br.com.kipperdev.clean.usecases;

import br.com.kipperdev.clean.entities.Enrollment;
import java.util.List;
import java.util.Optional;

/** Read, update and delete operations for already confirmed enrollments. */
public final class ManageEnrollments {
    private final EnrollmentRepository repository;

    public ManageEnrollments(EnrollmentRepository repository) { this.repository = repository; }

    public List<Enrollment> list() { return repository.findAll(); }

    public Optional<Enrollment> find(String id) { return repository.findById(id); }

    /** Student and course may change; id and paid amount remain immutable. */
    public Optional<Enrollment> updateDetails(String id, String student, String course) {
        return repository.findById(id).map(existing -> {
            var updated = new Enrollment(existing.id(), student, course, existing.amountInCents());
            repository.save(updated);
            return updated;
        });
    }

    public boolean delete(String id) {
        if (repository.findById(id).isEmpty()) return false;
        repository.deleteById(id);
        return true;
    }
}
