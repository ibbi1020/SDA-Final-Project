package com.block20.repositories;

import com.block20.models.Equipment;
import java.util.List;

public interface EquipmentRepository {
    void save(Equipment equipment);
    List<Equipment> findAll();
    Equipment findById(String id);
}