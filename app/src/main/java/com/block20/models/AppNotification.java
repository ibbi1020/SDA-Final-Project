package com.block20.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppNotification {
    private final String id;
    private final String title;
    private final String message;
    private final LocalDateTime timestamp;
    private boolean isRead;

    public AppNotification(String id, String title, String message) {
        this(id, title, message, LocalDateTime.now(), false);
    }

    public AppNotification(String id, String title, String message, LocalDateTime timestamp, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.isRead = isRead;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void markRead() {
        this.isRead = true;
    }

    public String getTimeFormatted() {
        return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
    }
}