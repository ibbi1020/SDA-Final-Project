package com.block20.services;

import com.block20.models.Trainer;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainerService {
    void registerTrainer(String firstName,
                            String lastName,
                            String email,
                            String phone,
                            String specialization,
                            String certification,
                            LocalDate hireDate,
                            String notes);
    List<Trainer> getAllTrainers();
    List<Trainer> searchTrainers(String keyword);
    Optional<Trainer> getTrainerById(String trainerId);
    void updateTrainerStatus(String trainerId, String status);
    void deleteTrainer(String trainerId);
}
