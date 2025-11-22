package com.block20.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class TrainerAvailabilitySlot {
    private final String slotId;
    private final String trainerId;
    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public TrainerAvailabilitySlot(String slotId, String trainerId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.slotId = slotId;
        this.trainerId = trainerId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean overlaps(LocalTime start, LocalTime end) {
        return !(end.isBefore(startTime) || end.equals(startTime) || start.isAfter(endTime) || start.equals(endTime));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainerAvailabilitySlot slot)) return false;
        return Objects.equals(slotId, slot.slotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotId);
    }
}
