package com.block20.repositories;

import com.block20.models.Trainer;
import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    void save(Trainer trainer);
    Optional<Trainer> findById(String trainerId);
    List<Trainer> findAll();
    List<Trainer> search(String keyword);
    void delete(String trainerId);
    void updateStatus(String trainerId, String status);
}
