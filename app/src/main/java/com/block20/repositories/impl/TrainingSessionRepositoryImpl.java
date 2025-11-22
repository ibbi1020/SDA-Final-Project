package com.block20.repositories.impl;

import com.block20.models.TrainingSession;
import com.block20.repositories.TrainingSessionRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class TrainingSessionRepositoryImpl implements TrainingSessionRepository {

    private final List<TrainingSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void save(TrainingSession session) {
        sessions.removeIf(existing -> existing.getSessionId().equals(session.getSessionId()));
        sessions.add(session);
    }

    @Override
    public List<TrainingSession> findAll() {
        return sessions.stream()
                .sorted(Comparator
                        .comparing(TrainingSession::getSessionDate)
                        .thenComparing(TrainingSession::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TrainingSession> findById(String sessionId) {
        return sessions.stream()
                .filter(session -> session.getSessionId().equals(sessionId))
                .findFirst();
    }

    @Override
    public List<TrainingSession> findByTrainerAndDate(String trainerId, LocalDate date) {
        return sessions.stream()
                .filter(session -> session.getTrainerId().equalsIgnoreCase(trainerId)
                        && session.getSessionDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingSession> findByMemberAndDate(String memberId, LocalDate date) {
        return sessions.stream()
                .filter(session -> session.getMemberId().equalsIgnoreCase(memberId)
                        && session.getSessionDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String sessionId) {
        sessions.removeIf(session -> session.getSessionId().equals(sessionId));
    }
}
