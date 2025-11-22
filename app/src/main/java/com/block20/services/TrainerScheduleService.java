package com.block20.services;

import com.block20.models.TrainerAvailabilitySlot;
import com.block20.models.TrainingSession;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TrainerScheduleService {

    TrainerAvailabilitySlot addAvailabilitySlot(String trainerId,
                                                DayOfWeek dayOfWeek,
                                                LocalTime startTime,
                                                LocalTime endTime);

    void removeAvailabilitySlot(String slotId);

    List<TrainerAvailabilitySlot> getAvailabilityForTrainer(String trainerId);

    TrainingSession scheduleSession(String memberId,
                                    String memberName,
                                    String trainerId,
                                    String sessionType,
                                    LocalDate sessionDate,
                                    LocalTime startTime,
                                    int durationMinutes,
                                    String notes);

    void cancelSession(String sessionId, String reason);

    List<TrainingSession> getAllSessions();

    Optional<TrainingSession> getSessionById(String sessionId);
}
