package com.block20.services.impl;

import com.block20.services.NotificationService;
import com.block20.services.notifications.EmailStrategy;
import com.block20.services.notifications.NotificationStrategy;
import com.block20.services.notifications.SMSStrategy;
import com.block20.models.AppNotification;
import com.block20.repositories.NotificationRepository;
import java.util.List;
import java.util.UUID;

public class NotificationServiceImpl implements NotificationService {

    private NotificationStrategy emailStrategy;
    private NotificationStrategy smsStrategy;
    private NotificationRepository notificationRepo;

    public NotificationServiceImpl(NotificationRepository notificationRepo) {
        this.emailStrategy = new EmailStrategy();
        this.smsStrategy = new SMSStrategy();
        this.notificationRepo = notificationRepo;
    }

    private void saveInternalAlert(String title, String message) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        notificationRepo.save(new AppNotification(id, title, message));
    }

    @Override
    public void sendWelcomePacket(String email, String name, String memberId) {
        String subject = "Welcome to Block20 Gym!";
        String body = "Hello " + name + ",\n\n" +
                "Welcome to the family! Your Member ID is: " + memberId + ".\n" +
                "You can now check in at the front desk.\n\n" +
                "Best,\nBlock20 Team";
        emailStrategy.send(email, subject, body);
        saveInternalAlert("New Enrollment", "Welcome packet sent to " + name);
    }

    @Override
    public void sendRenewalReceipt(String email, String name, double amount) {
        String subject = "Payment Receipt";
        String body = "Hello " + name + ",\n\n" +
                "Thank you for your payment of $" + amount + ".\n" +
                "Your membership has been successfully renewed.";
        emailStrategy.send(email, subject, body);
        saveInternalAlert("Payment Received", "Received $" + amount + " from " + name);
    }

    @Override
    public void sendSecurityAlert(String phone, String name, String issue) {
        // Use SMS for urgent security alerts
        String msg = "ALERT: " + name + ", your account status changed: " + issue + ". Contact support immediately.";
        smsStrategy.send(phone, "Security Alert", msg);
        saveInternalAlert("Security Alert", "Suspended member " + name + ": " + issue);
    }

    public List<AppNotification> getNotifications() {
        return notificationRepo.getRecentNotifications();
    }
    
    public int getUnreadCount() {
        return notificationRepo.getUnreadCount();
    }
}