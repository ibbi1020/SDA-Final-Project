package com.block20.services.impl;

import com.block20.models.Trainer;
import com.block20.repositories.TrainerRepository;
import com.block20.services.TrainerService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TrainerServiceImpl implements TrainerService {
    
    private final TrainerRepository trainerRepo;

    public TrainerServiceImpl(TrainerRepository trainerRepo) {
        this.trainerRepo = trainerRepo;
    }

    @Override
    public void registerTrainer(String firstName, String lastName, String email, String phone, 
                                String specialization, String certification, LocalDate hireDate, String notes) {
        
        String id = "TR" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        
        // Create the complex 13-field Trainer object
        // We set default values (0) for the stats fields
        Trainer t = new Trainer(
            id,
            firstName,
            lastName,
            email,
            phone,
            specialization,
            certification,
            "Active",   // Default status
            hireDate,
            0,          // sessionsPerMonth default
            0,          // activeClients default
            0,          // totalSessions default
            notes
        );
        
        trainerRepo.save(t);
        System.out.println("Service: Registered trainer " + firstName + " " + lastName);
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerRepo.findAll();
    }

    @Override
    public List<Trainer> searchTrainers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllTrainers();
        }
        // If Repo has specific search, use it. Otherwise filter here.
        return trainerRepo.search(keyword);
    }

    @Override
    public Optional<Trainer> getTrainerById(String trainerId) {
        return trainerRepo.findById(trainerId);
    }

    @Override
    public void updateTrainerStatus(String trainerId, String status) {
        trainerRepo.updateStatus(trainerId, status);
    }

    @Override
    public void deleteTrainer(String trainerId) {
        trainerRepo.delete(trainerId);
    }
}