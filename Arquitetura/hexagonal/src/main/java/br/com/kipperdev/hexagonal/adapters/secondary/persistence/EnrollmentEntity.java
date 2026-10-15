package br.com.kipperdev.hexagonal.adapters.secondary.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "enrollments")
public class EnrollmentEntity {
    @Id
    @Column(nullable = false, length = 120)
    private String id;
    @Column(nullable = false, length = 200)
    private String student;
    @Column(nullable = false, length = 200)
    private String course;
    @Column(name = "amount_in_cents", nullable = false)
    private int amountInCents;

    protected EnrollmentEntity() {}
    public EnrollmentEntity(String id, String student, String course, int amountInCents) {
        this.id = id; this.student = student; this.course = course; this.amountInCents = amountInCents;
    }
    public String getId() { return id; }
    public String getStudent() { return student; }
    public String getCourse() { return course; }
    public int getAmountInCents() { return amountInCents; }
    public void update(String student, String course, int amountInCents) {
        this.student = student; this.course = course; this.amountInCents = amountInCents;
    }
}
