package com.block20.repositories;

import com.block20.models.TrainingSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingSessionRepository {
    void save(TrainingSession session);
    List<TrainingSession> findAll();
    Optional<TrainingSession> findById(String sessionId);
    List<TrainingSession> findByTrainerAndDate(String trainerId, LocalDate date);
    List<TrainingSession> findByMemberAndDate(String memberId, LocalDate date);
    void delete(String sessionId);
}
