package br.com.kipperdev.hexagonal.application.ports.primary;

import br.com.kipperdev.hexagonal.domain.Enrollment;
import java.util.List;
import java.util.Optional;

/** Primary port for querying and managing confirmed enrollments. */
public interface ManageEnrollments {
    List<Enrollment> list();
    Optional<Enrollment> find(String id);
    Optional<Enrollment> updateDetails(String id, String student, String course);
    boolean delete(String id);
}
