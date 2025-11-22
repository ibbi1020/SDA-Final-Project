package com.block20.services.impl;

import com.block20.services.NotificationService;
import com.block20.services.notifications.EmailStrategy;
import com.block20.services.notifications.NotificationStrategy;
import com.block20.services.notifications.SMSStrategy;

public class NotificationServiceImpl implements NotificationService {
    
    private NotificationStrategy emailStrategy;
    private NotificationStrategy smsStrategy;

    public NotificationServiceImpl() {
        this.emailStrategy = new EmailStrategy();
        this.smsStrategy = new SMSStrategy();
    }

    @Override
    public void sendWelcomePacket(String email, String name, String memberId) {
        String subject = "Welcome to Block20 Gym!";
        String body = "Hello " + name + ",\n\n" +
                      "Welcome to the family! Your Member ID is: " + memberId + ".\n" +
                      "You can now check in at the front desk.\n\n" +
                      "Best,\nBlock20 Team";
        emailStrategy.send(email, subject, body);
    }

    @Override
    public void sendRenewalReceipt(String email, String name, double amount) {
        String subject = "Payment Receipt";
        String body = "Hello " + name + ",\n\n" +
                      "Thank you for your payment of $" + amount + ".\n" +
                      "Your membership has been successfully renewed.";
        emailStrategy.send(email, subject, body);
    }

    @Override
    public void sendSecurityAlert(String phone, String name, String issue) {
        // Use SMS for urgent security alerts
        String msg = "ALERT: " + name + ", your account status changed: " + issue + ". Contact support immediately.";
        smsStrategy.send(phone, "Security Alert", msg);
    }
}