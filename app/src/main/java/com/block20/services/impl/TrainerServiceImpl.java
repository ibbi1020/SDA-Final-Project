package com.block20.services.impl;

import com.block20.models.Trainer;
import com.block20.repositories.TrainerRepository;
import com.block20.services.TrainerService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;

    public TrainerServiceImpl(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Trainer registerTrainer(String firstName,
                                   String lastName,
                                   String email,
                                   String phone,
                                   String specialization,
                                   String certification,
                                   LocalDate hireDate,
                                   String notes) {
        validateRequiredFields(firstName, lastName, email, specialization);
        ensureEmailIsUnique(email);

        String trainerId = generateTrainerId();
        Trainer trainer = new Trainer(
                trainerId,
                capitalize(firstName),
                capitalize(lastName),
                email.trim(),
                phone != null ? phone.trim() : "",
                specialization,
                certification,
                "Active",
                hireDate != null ? hireDate : LocalDate.now(),
                0,
                0,
                0,
                notes
        );
        trainerRepository.save(trainer);
        return trainer;
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    @Override
    public List<Trainer> searchTrainers(String keyword) {
        return trainerRepository.search(keyword);
    }

    @Override
    public Optional<Trainer> getTrainerById(String trainerId) {
        return trainerRepository.findById(trainerId);
    }

    @Override
    public void updateTrainerStatus(String trainerId, String status) {
        trainerRepository.updateStatus(trainerId, status);
    }

    @Override
    public void deleteTrainer(String trainerId) {
        trainerRepository.delete(trainerId);
    }

    private void validateRequiredFields(String firstName, String lastName, String email, String specialization) {
        if (isBlank(firstName) || isBlank(lastName) || isBlank(email) || isBlank(specialization)) {
            throw new IllegalArgumentException("First name, last name, email, and specialization are required.");
        }
    }

    private void ensureEmailIsUnique(String email) {
        boolean exists = trainerRepository.findAll().stream()
                .anyMatch(t -> t.getEmail().equalsIgnoreCase(email.trim()));
        if (exists) {
            throw new IllegalArgumentException("Trainer with this email already exists.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String generateTrainerId() {
        return "T" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1);
    }
}
