package br.com.kipperdev.hexagonal.application.usecases;

import br.com.kipperdev.hexagonal.application.ports.primary.ManageEnrollments;
import br.com.kipperdev.hexagonal.application.ports.secondary.EnrollmentRepository;
import br.com.kipperdev.hexagonal.domain.Enrollment;
import java.util.List;
import java.util.Optional;

/** Use cases for confirmed enrollment queries, edits and removal. */
public final class ManageEnrollmentsService implements ManageEnrollments {
    private final EnrollmentRepository repository;

    public ManageEnrollmentsService(EnrollmentRepository repository) { this.repository = repository; }

    @Override public List<Enrollment> list() { return repository.findAll(); }
    @Override public Optional<Enrollment> find(String id) { return repository.findById(id); }

    /** Student and course may change; id and paid amount remain immutable. */
    @Override public Optional<Enrollment> updateDetails(String id, String student, String course) {
        return repository.findById(id).map(existing -> {
            var updated = new Enrollment(existing.id(), student, course, existing.amountInCents());
            repository.save(updated);
            return updated;
        });
    }

    @Override public boolean delete(String id) {
        if (repository.findById(id).isEmpty()) return false;
        repository.deleteById(id);
        return true;
    }
}
