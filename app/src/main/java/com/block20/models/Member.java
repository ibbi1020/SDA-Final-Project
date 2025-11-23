package com.block20.models;

import java.time.LocalDate;

public class Member {
    private String memberId;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private String planType;
    private LocalDate joinDate;
    private LocalDate expiryDate;
    
    // New Fields
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;

    // Constructor for Loading from DB (Full)
    public Member(String memberId, String fullName, String email, String phone, String planType, 
                  String address, String emName, String emPhone, String emRel) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.planType = planType;
        this.status = "Active";
        this.joinDate = LocalDate.now();
        this.expiryDate = LocalDate.now().plusMonths(1);
        
        this.address = address;
        this.emergencyContactName = emName;
        this.emergencyContactPhone = emPhone;
        this.emergencyContactRelationship = emRel;
    }

    // Constructor for New Registration (Simple)
    public Member(String memberId, String fullName, String email, String phone, String planType) {
        this(memberId, fullName, email, phone, planType, "", "", "", "");
    }

    // Getters
    public String getMemberId() { return memberId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public String getPlanType() { return planType; }
    public LocalDate getJoinDate() { return joinDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    
    // New Getters (Fixes "cannot find symbol")
    public String getAddress() { return address; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public String getEmergencyContactRelationship() { return emergencyContactRelationship; }
    // Helper alias if code uses "Relation"
    public String getEmergencyContactRelation() { return emergencyContactRelationship; } 

    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setStatus(String status) { this.status = status; }
    public void setPlanType(String planType) { this.planType = planType; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    
    public void setAddress(String address) { this.address = address; }
    public void setEmergencyContactName(String n) { this.emergencyContactName = n; }
    public void setEmergencyContactPhone(String p) { this.emergencyContactPhone = p; }
    public void setEmergencyContactRelationship(String r) { this.emergencyContactRelationship = r; }
    // Helper alias
    public void setEmergencyContactRelation(String r) { this.emergencyContactRelationship = r; }
}