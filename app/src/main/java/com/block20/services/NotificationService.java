package com.block20.services;

import com.block20.models.AppNotification; // <--- Add Import
import java.util.List;                     // <--- Add Import

public interface NotificationService {
    void sendWelcomePacket(String email, String name, String memberId);
    void sendRenewalReceipt(String email, String name, double amount);
    void sendSecurityAlert(String phone, String name, String issue);
    
    // --- ADD THESE MISSING METHODS ---
    List<AppNotification> getNotifications();
    int getUnreadCount();
}