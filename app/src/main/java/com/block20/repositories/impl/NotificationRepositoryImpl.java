package com.block20.repositories.impl;

import com.block20.models.AppNotification;
import com.block20.repositories.NotificationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationRepositoryImpl implements NotificationRepository {
    private List<AppNotification> storage = new ArrayList<>();

    @Override
    public void save(AppNotification notification) {
        // Add to top of list (newest first)
        storage.add(0, notification);
    }

    @Override
    public List<AppNotification> getRecentNotifications() {
        // Return top 10
        return storage.stream().limit(10).collect(Collectors.toList());
    }

    @Override
    public int getUnreadCount() {
        return (int) storage.stream().filter(n -> !n.isRead()).count();
    }
}