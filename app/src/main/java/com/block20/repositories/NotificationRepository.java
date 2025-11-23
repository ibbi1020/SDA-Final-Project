package com.block20.repositories;

import com.block20.models.AppNotification;
import java.util.List;

public interface NotificationRepository {
    void save(AppNotification notification);
    List<AppNotification> getRecentNotifications();
    int getUnreadCount();
}