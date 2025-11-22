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

    public Member(String memberId, String fullName, String email, String phone, String planType) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.planType = planType;
        
        // Defaults
        this.status = "Active";
        this.joinDate = LocalDate.now();
        this.expiryDate = LocalDate.now().plusMonths(1);
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
    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setStatus(String status) { this.status = status; }
    public void setPlanType(String planType) { this.planType = planType; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

}