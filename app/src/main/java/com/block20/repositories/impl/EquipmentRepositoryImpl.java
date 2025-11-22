package com.block20.repositories.impl;

import com.block20.models.Equipment;
import com.block20.repositories.EquipmentRepository;
import java.util.ArrayList;
import java.util.List;

public class EquipmentRepositoryImpl implements EquipmentRepository {
    private List<Equipment> inventory = new ArrayList<>();

    @Override
    public void save(Equipment equipment) {
        // Upsert Logic (Update if exists, Insert if new)
        boolean exists = false;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getEquipmentId().equals(equipment.getEquipmentId())) {
                inventory.set(i, equipment);
                exists = true;
                break;
            }
        }
        if (!exists) {
            inventory.add(equipment);
        }
    }

    @Override
    public List<Equipment> findAll() {
        return new ArrayList<>(inventory);
    }

    @Override
    public Equipment findById(String id) {
        return inventory.stream()
                .filter(e -> e.getEquipmentId().equals(id))
                .findFirst()
                .orElse(null);
    }
}