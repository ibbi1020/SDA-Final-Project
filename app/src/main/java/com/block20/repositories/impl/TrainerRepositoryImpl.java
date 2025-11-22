package com.block20.repositories.impl;

import com.block20.models.Trainer;
import com.block20.repositories.TrainerRepository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Temporary in-memory implementation. We'll switch to SQLite later.
 */
public class TrainerRepositoryImpl implements TrainerRepository {

    private final List<Trainer> trainerTable = new ArrayList<>();

    @Override
    public synchronized void save(Trainer trainer) {
        delete(trainer.getTrainerId());
        trainerTable.add(trainer);
    }

    @Override
    public synchronized Optional<Trainer> findById(String trainerId) {
        return trainerTable.stream()
                .filter(t -> t.getTrainerId().equalsIgnoreCase(trainerId))
                .findFirst();
    }

    @Override
    public synchronized List<Trainer> findAll() {
        return trainerTable.stream()
                .sorted((a, b) -> a.getLastName().compareToIgnoreCase(b.getLastName()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<Trainer> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String lower = keyword.toLowerCase();
        return trainerTable.stream()
                .filter(t -> t.getFullName().toLowerCase().contains(lower)
                        || t.getEmail().toLowerCase().contains(lower)
                        || (t.getSpecialization() != null && t.getSpecialization().toLowerCase().contains(lower))
                        || t.getTrainerId().toLowerCase().contains(lower))
                .sorted((a, b) -> a.getLastName().compareToIgnoreCase(b.getLastName()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void delete(String trainerId) {
        Iterator<Trainer> iterator = trainerTable.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getTrainerId().equalsIgnoreCase(trainerId)) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public synchronized void updateStatus(String trainerId, String status) {
        findById(trainerId).ifPresent(trainer -> trainer.setStatus(status));
    }
}
