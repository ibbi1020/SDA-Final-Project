package com.block20.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLog {
    private String logId;
    private String targetId;   // The Member ID affected
    private String action;     // e.g., "UPDATE", "STATUS_CHANGE"
    private String details;    // e.g., "Name changed from John to Jonathan"
    private LocalDateTime timestamp;

    public AuditLog(String logId, String targetId, String action, String details) {
        this.logId = logId;
        this.targetId = targetId;
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getTargetId() { return targetId; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public String getTimestampFormatted() { 
        return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")); 
    }
}