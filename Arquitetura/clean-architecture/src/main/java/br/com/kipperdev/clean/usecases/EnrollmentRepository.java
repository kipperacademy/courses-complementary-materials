package br.com.kipperdev.clean.usecases;

import br.com.kipperdev.clean.entities.Enrollment;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    List<Enrollment> findAll();
    Optional<Enrollment> findById(String id);
    void deleteById(String id);
}
