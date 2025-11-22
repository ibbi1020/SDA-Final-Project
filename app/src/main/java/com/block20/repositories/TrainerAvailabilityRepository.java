package com.block20.repositories;

import com.block20.models.TrainerAvailabilitySlot;
import java.time.DayOfWeek;
import java.util.List;

public interface TrainerAvailabilityRepository {
    void save(TrainerAvailabilitySlot slot);
    List<TrainerAvailabilitySlot> findByTrainer(String trainerId);
    List<TrainerAvailabilitySlot> findByTrainerAndDay(String trainerId, DayOfWeek dayOfWeek);
    void delete(String slotId);
}
