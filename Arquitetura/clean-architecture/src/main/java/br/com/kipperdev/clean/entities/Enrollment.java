package br.com.kipperdev.clean.entities;

public record Enrollment(String id, String student, String course, int amountInCents) {
    public Enrollment {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Enrollment id is required");
        if (student == null || student.isBlank()) throw new IllegalArgumentException("Student is required");
        if (course == null || course.isBlank()) throw new IllegalArgumentException("Course is required");
        if (amountInCents <= 0) throw new IllegalArgumentException("Amount must be positive cents");
    }
}
