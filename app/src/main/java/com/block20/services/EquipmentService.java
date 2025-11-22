package com.block20.services;

import com.block20.models.Equipment;
import java.util.List;

public interface EquipmentService {
    void addEquipment(String name, String category, String status);
    void updateStatus(String id, String newStatus);
    List<Equipment> getInventory();
}