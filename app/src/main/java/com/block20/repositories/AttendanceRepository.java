package com.block20.repositories;

import com.block20.models.Attendance;
import java.util.List;

public interface AttendanceRepository {
    int countActiveVisits();
    void save(Attendance attendance);
    List<Attendance> findAll();
    List<Attendance> findByMemberId(String memberId);
    Attendance findActiveVisitByMemberId(String memberId);
}