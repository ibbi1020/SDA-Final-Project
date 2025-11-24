package com.block20.services.impl;

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
import java.util.stream.Collectors;

public class TrainerScheduleServiceImpl implements TrainerScheduleService {

    private final TrainerService trainerService;
    private final TrainerAvailabilityRepository availabilityRepo;
    private final TrainingSessionRepository sessionRepo;

    public TrainerScheduleServiceImpl(TrainerService trainerService,
                                      TrainerAvailabilityRepository availabilityRepo,
                                      TrainingSessionRepository sessionRepo) {
        this.trainerService = trainerService;
        this.availabilityRepo = availabilityRepo;
        this.sessionRepo = sessionRepo;
    }

    @Override
    public TrainerAvailabilitySlot addAvailabilitySlot(String trainerId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        // Basic validation
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        String slotId = UUID.randomUUID().toString();
        TrainerAvailabilitySlot slot = new TrainerAvailabilitySlot(slotId, trainerId, dayOfWeek, startTime, endTime);
        availabilityRepo.save(slot);
        return slot;
    }

    @Override
    public void removeAvailabilitySlot(String slotId) {
        availabilityRepo.delete(slotId);
    }

    @Override
    public List<TrainerAvailabilitySlot> getAvailabilityForTrainer(String trainerId) {
        return availabilityRepo.findByTrainer(trainerId);
    }

    // --- THE SMART LOGIC ---
    @Override
    public boolean isSlotAvailable(String trainerId, LocalDate date, LocalTime startTime, int durationMinutes) {
        LocalTime endTime = startTime.plusMinutes(durationMinutes);
        DayOfWeek day = date.getDayOfWeek();

        // 1. Check if Trainer is working (Shift Check)
        List<TrainerAvailabilitySlot> shifts = availabilityRepo.findByTrainerAndDay(trainerId, day);
        
        boolean isWorking = false;
        for (TrainerAvailabilitySlot shift : shifts) {
            // Check if requested time fits INSIDE the shift
            // (Start >= ShiftStart AND End <= ShiftEnd)
            if (!startTime.isBefore(shift.getStartTime()) && !endTime.isAfter(shift.getEndTime())) {
                isWorking = true;
                break;
            }
        }
        
        if (!isWorking) return false; // Trainer isn't working at this time

        // 2. Check for Double Booking (Conflict Check)
        List<TrainingSession> existingSessions = sessionRepo.findByTrainerAndDate(trainerId, date);
        
        for (TrainingSession s : existingSessions) {
            // Skip cancelled sessions
            if ("Cancelled".equalsIgnoreCase(s.getStatus())) continue;

            LocalTime sStart = s.getStartTime();
            LocalTime sEnd = s.getStartTime().plusMinutes(s.getDurationMinutes());

            // Overlap Logic: (StartA < EndB) and (EndA > StartB)
            if (startTime.isBefore(sEnd) && endTime.isAfter(sStart)) {
                return false; // Conflict found!
            }
        }

        return true; // Slot is clean
    }

    @Override
    public TrainingSession scheduleSession(String memberId, String memberName, String trainerId, String sessionType,
                                           LocalDate sessionDate, LocalTime startTime, int durationMinutes, String notes) {
        
        // 1. Verify Availability
        if (!isSlotAvailable(trainerId, sessionDate, startTime, durationMinutes)) {
            throw new IllegalStateException("Trainer is not available at this time.");
        }

        // 2. Get Trainer Details
        var trainer = trainerService.getTrainerById(trainerId)
            .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

        // 3. Create Session
        String sessionId = "SES" + System.currentTimeMillis();
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
            notes
        );

        // 4. Save
        sessionRepo.save(session);
        System.out.println("Session Booked: " + sessionId);
        
        return session;
    }

    @Override
    public void cancelSession(String sessionId, String reason) {
        Optional<TrainingSession> opt = sessionRepo.findById(sessionId);
        if (opt.isPresent()) {
            TrainingSession s = opt.get();
            s.setStatus("Cancelled");
            // Append cancellation reason to notes
            // (In a real app, we might have a dedicated cancellation reason field)
            // s.setNotes(s.getNotes() + " [Cancelled: " + reason + "]"); 
            sessionRepo.save(s);
        }
    }

    @Override
    public List<TrainingSession> getAllSessions() {
        return sessionRepo.findAll();
    }

    @Override
    public Optional<TrainingSession> getSessionById(String sessionId) {
        return sessionRepo.findById(sessionId);
    }

    @Override
    public List<TrainingSession> getSessionsForMember(String memberId) {
        // Since repository might not have findByMember (or returns by date), we filter all
        // Ideally, add findByMemberId to Repository for efficiency
        return sessionRepo.findAll().stream()
                .filter(s -> s.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }
}