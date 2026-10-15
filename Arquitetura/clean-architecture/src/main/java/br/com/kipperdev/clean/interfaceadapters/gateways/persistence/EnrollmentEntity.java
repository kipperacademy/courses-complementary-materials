package br.com.kipperdev.clean.interfaceadapters.gateways.persistence;

import br.com.kipperdev.clean.entities.Enrollment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
public class EnrollmentEntity {
    @Id @Column(nullable = false, length = 120) private String id;
    @Column(nullable = false, length = 200) private String student;
    @Column(nullable = false, length = 200) private String course;
    @Column(name = "amount_in_cents", nullable = false) private int amountInCents;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;

    protected EnrollmentEntity() {}

    public EnrollmentEntity(Enrollment enrollment) {
        this.id = enrollment.id(); this.student = enrollment.student(); this.course = enrollment.course();
        this.amountInCents = enrollment.amountInCents(); this.createdAt = LocalDateTime.now();
    }

    public void updateDetails(String student, String course) {
        this.student = student;
        this.course = course;
    }

    public Enrollment toDomain() { return new Enrollment(id, student, course, amountInCents); }
    public String getId() { return id; }
    public String getStudent() { return student; }
    public String getCourse() { return course; }
    public int getAmountInCents() { return amountInCents; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
