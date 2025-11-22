package com.block20.repositories.impl;

import com.block20.models.TrainerAvailabilitySlot;
import com.block20.repositories.TrainerAvailabilityRepository;
import java.time.DayOfWeek;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class TrainerAvailabilityRepositoryImpl implements TrainerAvailabilityRepository {

    private final List<TrainerAvailabilitySlot> slots = new CopyOnWriteArrayList<>();

    @Override
    public void save(TrainerAvailabilitySlot slot) {
        slots.removeIf(existing -> existing.getSlotId().equals(slot.getSlotId()));
        slots.add(slot);
    }

    @Override
    public List<TrainerAvailabilitySlot> findByTrainer(String trainerId) {
        return slots.stream()
                .filter(slot -> slot.getTrainerId().equalsIgnoreCase(trainerId))
                .sorted((a, b) -> a.getDayOfWeek().compareTo(b.getDayOfWeek()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainerAvailabilitySlot> findByTrainerAndDay(String trainerId, DayOfWeek dayOfWeek) {
        return slots.stream()
                .filter(slot -> slot.getTrainerId().equalsIgnoreCase(trainerId)
                        && slot.getDayOfWeek() == dayOfWeek)
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String slotId) {
        slots.removeIf(slot -> slot.getSlotId().equals(slotId));
    }
}
