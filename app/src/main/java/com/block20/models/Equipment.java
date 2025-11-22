package com.block20.models;

import java.time.LocalDate;

public class Equipment {
    private String equipmentId;
    private String name;
    private String category; // e.g., "Cardio", "Strength"
    private String status;   // "Functional", "Maintenance", "Broken"
    private LocalDate purchaseDate;

    public Equipment(String equipmentId, String name, String category, String status) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.category = category;
        this.status = status;
        this.purchaseDate = LocalDate.now();
    }

    // Getters and Setters
    public String getEquipmentId() { return equipmentId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public LocalDate getPurchaseDate() { return purchaseDate; }

    public void setStatus(String status) { this.status = status; }
}