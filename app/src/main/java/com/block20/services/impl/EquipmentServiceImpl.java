package com.block20.services.impl;

import com.block20.models.Equipment;
import com.block20.repositories.EquipmentRepository;
import com.block20.services.EquipmentService;
import java.util.List;
import java.util.UUID;

public class EquipmentServiceImpl implements EquipmentService {
    
    private EquipmentRepository equipmentRepo;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepo) {
        this.equipmentRepo = equipmentRepo;
    }

    @Override
    public void addEquipment(String name, String category, String status) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name required");
        
        String id = "EQ" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        Equipment equipment = new Equipment(id, name, category, status);
        equipmentRepo.save(equipment);
    }

    @Override
    public void updateStatus(String id, String newStatus) {
        Equipment eq = equipmentRepo.findById(id);
        if (eq == null) throw new IllegalArgumentException("Equipment not found");
        eq.setStatus(newStatus);
        equipmentRepo.save(eq); // Save updates
    }

    @Override
    public List<Equipment> getInventory() {
        return equipmentRepo.findAll();
    }
}