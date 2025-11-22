package com.block20.services.notifications;

public class SMSStrategy implements NotificationStrategy {
    @Override
    public void send(String recipient, String subject, String message) {
        // In a real app, this would use Twilio API
        System.out.println("\n[SMS SERVICE] >>> To: " + recipient + " | Msg: " + message);
    }
}