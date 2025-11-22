package com.block20.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain model representing a gym trainer/coach.
 */
public class Trainer {
    private String trainerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String specialization;
    private String certification;
    private String status;
    private LocalDate hireDate;
    private int sessionsPerMonth;
    private int activeClients;
    private int totalSessions;
    private String notes;

    public Trainer(String trainerId,
                   String firstName,
                   String lastName,
                   String email,
                   String phone,
                   String specialization,
                   String certification,
                   String status,
                   LocalDate hireDate,
                   int sessionsPerMonth,
                   int activeClients,
                   int totalSessions,
                   String notes) {
        this.trainerId = trainerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.certification = certification;
        this.status = status;
        this.hireDate = hireDate;
        this.sessionsPerMonth = sessionsPerMonth;
        this.activeClients = activeClients;
        this.totalSessions = totalSessions;
        this.notes = notes;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public int getSessionsPerMonth() {
        return sessionsPerMonth;
    }

    public void setSessionsPerMonth(int sessionsPerMonth) {
        this.sessionsPerMonth = sessionsPerMonth;
    }

    public int getActiveClients() {
        return activeClients;
    }

    public void setActiveClients(int activeClients) {
        this.activeClients = activeClients;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFullName() {
        return (firstName + " " + lastName).trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Trainer trainer)) {
            return false;
        }
        return Objects.equals(trainerId, trainer.trainerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainerId);
    }
}
