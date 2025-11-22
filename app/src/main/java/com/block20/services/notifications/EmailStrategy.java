package com.block20.services.notifications;

public class EmailStrategy implements NotificationStrategy {
    @Override
    public void send(String recipient, String subject, String message) {
        // In a real app, this would use JavaMail API
        System.out.println("\n[EMAIL SERVICE] --------------------------");
        System.out.println("TO: " + recipient);
        System.out.println("SUBJECT: " + subject);
        System.out.println("BODY: " + message);
        System.out.println("------------------------------------------\n");
    }
}