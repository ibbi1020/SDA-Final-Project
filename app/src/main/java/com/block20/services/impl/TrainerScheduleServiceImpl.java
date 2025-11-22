package com.block20.services.impl;

import com.block20.models.Trainer;
import com.block20.models.TrainerAvailabilitySlot;
import com.block20.models.TrainingSession;
import com.block20.repositories.TrainerAvailabilityRepository;
import com.block20.repositories.TrainingSessionRepository;
import com.block20.services.TrainerScheduleService;
import com.block20.services.TrainerService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TrainerScheduleServiceImpl implements TrainerScheduleService {

    private final TrainerService trainerService;
    private final TrainerAvailabilityRepository availabilityRepository;
    private final TrainingSessionRepository trainingSessionRepository;

    public TrainerScheduleServiceImpl(TrainerService trainerService,
                                      TrainerAvailabilityRepository availabilityRepository,
                                      TrainingSessionRepository trainingSessionRepository) {
        this.trainerService = trainerService;
        this.availabilityRepository = availabilityRepository;
        this.trainingSessionRepository = trainingSessionRepository;
    }

    @Override
    public TrainerAvailabilitySlot addAvailabilitySlot(String trainerId,
                                                       DayOfWeek dayOfWeek,
                                                       LocalTime startTime,
                                                       LocalTime endTime) {
        validateTrainerExists(trainerId);
        if (dayOfWeek == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Day, start time, and end time are required.");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }

        boolean overlaps = availabilityRepository.findByTrainerAndDay(trainerId, dayOfWeek).stream()
                .anyMatch(slot -> startTime.isBefore(slot.getEndTime()) && endTime.isAfter(slot.getStartTime()));
        if (overlaps) {
            throw new IllegalArgumentException("Availability slot overlaps with an existing slot.");
        }

        String slotId = generateAvailabilitySlotId();
        TrainerAvailabilitySlot slot = new TrainerAvailabilitySlot(slotId, trainerId, dayOfWeek, startTime, endTime);
        availabilityRepository.save(slot);
        return slot;
    }

    @Override
    public void removeAvailabilitySlot(String slotId) {
        if (slotId == null || slotId.isBlank()) {
            throw new IllegalArgumentException("Slot id is required.");
        }
        availabilityRepository.delete(slotId);
    }

    @Override
    public List<TrainerAvailabilitySlot> getAvailabilityForTrainer(String trainerId) {
        return availabilityRepository.findByTrainer(trainerId);
    }

    @Override
    public TrainingSession scheduleSession(String memberId,
                                           String memberName,
                                           String trainerId,
                                           String sessionType,
                                           LocalDate sessionDate,
                                           LocalTime startTime,
                                           int durationMinutes,
                                           String notes) {
        validateScheduleRequest(memberId, memberName, trainerId, sessionType, sessionDate, startTime, durationMinutes);

        Trainer trainer = trainerService.getTrainerById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found."));

        LocalTime endTime = startTime.plusMinutes(durationMinutes);
        DayOfWeek dayOfWeek = sessionDate.getDayOfWeek();

        availabilityRepository.findByTrainerAndDay(trainerId, dayOfWeek).stream()
            .filter(slot -> !startTime.isBefore(slot.getStartTime()) && !endTime.isAfter(slot.getEndTime()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Trainer is not available at the requested time."));

        boolean trainerConflict = trainingSessionRepository.findByTrainerAndDate(trainerId, sessionDate).stream()
            .filter(this::isBlockingSession)
            .anyMatch(existing -> sessionsOverlap(existing, startTime, endTime));
        if (trainerConflict) {
            throw new IllegalStateException("Trainer already has a session scheduled in that time window.");
        }

        boolean memberConflict = trainingSessionRepository.findByMemberAndDate(memberId, sessionDate).stream()
            .filter(this::isBlockingSession)
            .anyMatch(existing -> sessionsOverlap(existing, startTime, endTime));
        if (memberConflict) {
            throw new IllegalStateException("Member already has a session scheduled in that time window.");
        }

        String sessionId = generateSessionId();
        TrainingSession session = new TrainingSession(
                sessionId,
                trainerId,
                trainer.getFullName(),
                memberId,
                memberName,
                sessionType,
                sessionDate,
                startTime,
                durationMinutes,
                "Scheduled",
                notes != null ? notes.trim() : ""
        );

        trainingSessionRepository.save(session);
        return session;
    }

    @Override
    public void cancelSession(String sessionId, String reason) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Session id is required.");
        }
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));
        if ("Cancelled".equalsIgnoreCase(session.getStatus())) {
            return;
        }
        session.setStatus("Cancelled");
        trainingSessionRepository.save(session);
        // Reason can be logged/persisted when audit infrastructure is added.
    }

    @Override
    public List<TrainingSession> getAllSessions() {
        return trainingSessionRepository.findAll();
    }

    @Override
    public Optional<TrainingSession> getSessionById(String sessionId) {
        return trainingSessionRepository.findById(sessionId);
    }

    private void validateTrainerExists(String trainerId) {
        if (trainerId == null || trainerId.isBlank()) {
            throw new IllegalArgumentException("Trainer id is required.");
        }
        trainerService.getTrainerById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found."));
    }

    private void validateScheduleRequest(String memberId,
                                         String memberName,
                                         String trainerId,
                                         String sessionType,
                                         LocalDate sessionDate,
                                         LocalTime startTime,
                                         int durationMinutes) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("Member id is required.");
        }
        if (memberName == null || memberName.isBlank()) {
            throw new IllegalArgumentException("Member name is required.");
        }
        validateTrainerExists(trainerId);
        if (sessionType == null || sessionType.isBlank()) {
            throw new IllegalArgumentException("Session type is required.");
        }
        if (sessionDate == null || startTime == null) {
            throw new IllegalArgumentException("Session date and start time are required.");
        }
        if (sessionDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot schedule sessions in the past.");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero.");
        }
        if (durationMinutes % 15 != 0) {
            throw new IllegalArgumentException("Duration must be in 15-minute increments.");
        }
    }

    private boolean sessionsOverlap(TrainingSession existing, LocalTime requestedStart, LocalTime requestedEnd) {
        LocalTime existingStart = existing.getStartTime();
        LocalTime existingEnd = existing.getEndTime();
        return requestedStart.isBefore(existingEnd) && requestedEnd.isAfter(existingStart);
    }

    private boolean isBlockingSession(TrainingSession session) {
        String status = session.getStatus() != null ? session.getStatus() : "";
        return "Scheduled".equalsIgnoreCase(status) || "In Progress".equalsIgnoreCase(status);
    }

    private String generateAvailabilitySlotId() {
        return "ASLOT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateSessionId() {
        return "SESS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
