package com.hospitalqueue.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// @Entity tells Spring/Hibernate: "this class maps to a database table."
// @Table names that table explicitly (otherwise it would default to "token").
@Entity
@Table(name = "tokens")
public class Token {

    // @Id marks the primary key.
    // @GeneratedValue(IDENTITY) means MySQL auto-increments this column itself
    // (this matches MySQL's AUTO_INCREMENT behavior).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientName;

    private int age;

    private String gender;

    // Kept as a plain String (not a relationship to another table) on purpose --
    // simple and enough for a first JPA project. Example values: "General",
    // "Cardiology", "Orthopedic", "Emergency".
    private String department;

    // The queue number shown to the patient, e.g. Token #7.
    private int tokenNumber;

    // true = this patient should be served before non-priority patients
    // (e.g. senior citizens, emergency cases).
    private boolean priority;

    // @Enumerated(STRING) tells Hibernate to store the enum as readable text
    // ("WAITING") in the database column, instead of a number (0, 1, 2...).
    // STRING is safer: if you ever reorder the enum values, existing data
    // in the database still means the same thing.
    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    // Soft delete flag. Instead of actually removing a row from the database
    // with DELETE, we just flip this to true and filter it out of normal
    // queries. This matches the "soft delete" pattern from your course.
    private boolean deleted;

    private LocalDateTime createdAt;

    // JPA REQUIRES a no-argument constructor -- it uses this internally
    // (via reflection) to create empty objects before filling in the fields
    // it reads from the database. You never call this yourself.
    public Token() {
    }

    // ===== Getters and setters =====
    // This is basic OOP encapsulation: fields are private, and the ONLY way
    // to read or change them from outside this class is through these
    // methods. Spring Boot also relies on these: when it turns incoming
    // JSON into a Token object, it calls these setters; when it turns a
    // Token object back into outgoing JSON, it calls these getters.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public void setStatus(TokenStatus status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
