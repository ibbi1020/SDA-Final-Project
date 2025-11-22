package com.block20.models;

import java.time.LocalDateTime;

public class Attendance {
    private String visitId;
    private String memberId;
    private String memberName; // Storing name here for easier display
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    public Attendance(String visitId, String memberId, String memberName) {
        this.visitId = visitId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.checkInTime = LocalDateTime.now();
    }

    // Getters
    public String getVisitId() { return visitId; }
    public String getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public LocalDateTime getCheckInTime() { return checkInTime; }
    public LocalDateTime getCheckOutTime() { return checkOutTime; }

    // Setters
    public void setCheckOutTime(LocalDateTime checkOutTime) { 
        this.checkOutTime = checkOutTime; 
    }
}