package com.block20.services;

public interface NotificationService {
    void sendWelcomePacket(String email, String name, String memberId);
    void sendRenewalReceipt(String email, String name, double amount);
    void sendSecurityAlert(String phone, String name, String issue);
}