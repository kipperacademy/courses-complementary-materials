package br.com.kipperdev.clean.interfaceadapters.gateways;

import br.com.kipperdev.clean.usecases.EnrollmentRepository;
import br.com.kipperdev.clean.entities.Enrollment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class InMemoryEnrollmentRepository implements EnrollmentRepository {
    private final List<Enrollment> enrollments = new ArrayList<>();

    @Override public void save(Enrollment enrollment) {
        enrollments.removeIf(existing -> existing.id().equals(enrollment.id()));
        enrollments.add(enrollment);
    }
    @Override public List<Enrollment> findAll() { return List.copyOf(enrollments); }
    @Override public Optional<Enrollment> findById(String id) {
        return enrollments.stream().filter(enrollment -> enrollment.id().equals(id)).findFirst();
    }
    @Override public void deleteById(String id) { enrollments.removeIf(enrollment -> enrollment.id().equals(id)); }
}
