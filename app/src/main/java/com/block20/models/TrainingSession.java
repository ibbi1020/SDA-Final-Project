package com.block20.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class TrainingSession {
    private String sessionId;
    private String trainerId;
    private String trainerName;
    private String memberId;
    private String memberName;
    private String sessionType;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private int durationMinutes;
    private String status; // Scheduled, Completed, Cancelled
    private final String notes;

    public TrainingSession(String sessionId,
                           String trainerId,
                           String trainerName,
                           String memberId,
                           String memberName,
                           String sessionType,
                           LocalDate sessionDate,
                           LocalTime startTime,
                           int durationMinutes,
                           String status,
                           String notes) {
        this.sessionId = sessionId;
        this.trainerId = trainerId;
        this.trainerName = trainerName;
        this.memberId = memberId;
        this.memberName = memberName;
        this.sessionType = sessionType;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.status = status;
        this.notes = notes;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getSessionType() {
        return sessionType;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalTime getEndTime() {
        return startTime.plusMinutes(durationMinutes);
    }

    public String getNotes() {
        return notes;
    }

    public boolean overlaps(LocalDate date, LocalTime start, LocalTime end) {
        if (!sessionDate.equals(date)) {
            return false;
        }
        return start.isBefore(getEndTime()) && end.isAfter(startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainingSession session)) return false;
        return Objects.equals(sessionId, session.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }
}
