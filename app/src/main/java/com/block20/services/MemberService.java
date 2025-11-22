package com.block20.services;

import com.block20.models.Member;
import com.block20.models.Attendance;
import java.util.List;

public interface MemberService {
    // CHANGE: Return 'Member' instead of 'void'
    Member registerMember(String fullName, String email, String phone, String planType);
    List<Member> getAllMembers();
    // Add this new method
    Attendance checkInMember(String memberId);
    void checkOutMember(String memberId);
    boolean isMemberCheckedIn(String memberId);
    int getCurrentOccupancyCount();
    void renewMembership(String memberId, String newPlanType);
}