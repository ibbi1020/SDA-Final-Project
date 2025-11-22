package com.block20.services;

import com.block20.models.AuditLog;
import java.util.List;

public interface AuditService {
    void logAction(String targetId, String action, String details);
    List<AuditLog> getLogsForMember(String memberId);
}