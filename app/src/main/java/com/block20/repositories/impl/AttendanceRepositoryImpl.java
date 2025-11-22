package com.block20.repositories.impl;

import com.block20.models.Attendance;
import com.block20.repositories.AttendanceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceRepositoryImpl implements AttendanceRepository {
    
    private List<Attendance> attendanceTable = new ArrayList<>();

    @Override
    public void save(Attendance attendance) {
        attendanceTable.add(attendance);
        System.out.println("DEBUG: Saved Visit. Total visits in DB: " + attendanceTable.size());
        System.out.println("DEBUG: Visit Details -> Member: " + attendance.getMemberId() + ", Time: " + attendance.getCheckInTime());
    }

    @Override
    public List<Attendance> findAll() {
        return new ArrayList<>(attendanceTable);
    }

    @Override
    public List<Attendance> findByMemberId(String memberId) {
        return attendanceTable.stream()
                .filter(a -> a.getMemberId().equalsIgnoreCase(memberId)) // Fix: Ignore Case
                .collect(Collectors.toList());
    }

    @Override
    public Attendance findActiveVisitByMemberId(String memberId) {
        System.out.println("DEBUG: Searching for active visit for Member: " + memberId);
        for (Attendance a : attendanceTable) {
            // Fix: Ignore Case for IDs and check for null Checkout Time
            if (a.getMemberId().equalsIgnoreCase(memberId) && a.getCheckOutTime() == null) {
                System.out.println("DEBUG: Found Active Visit! ID: " + a.getVisitId());
                return a;
            }
        }
        System.out.println("DEBUG: No active visit found.");
        return null;
    }
    @Override
    public int countActiveVisits() {
        int count = 0;
        for (Attendance a : attendanceTable) {
            // If checkout time is null, they are still in the gym
            if (a.getCheckOutTime() == null) {
                count++;
            }
        }
        return count;
    }
}