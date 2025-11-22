package com.block20.services.notifications;

public interface NotificationStrategy {
    void send(String recipient, String subject, String message);
}