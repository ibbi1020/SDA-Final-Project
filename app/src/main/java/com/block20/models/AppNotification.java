package com.block20.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppNotification {
    private String id;
    private String title;
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;

    public AppNotification(String id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public void markRead() { this.isRead = true; }
    
    public String getTimeFormatted() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}