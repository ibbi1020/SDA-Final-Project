package com.block20.services.impl;

import com.block20.models.Attendance;
import com.block20.models.Member;
import com.block20.repositories.AttendanceRepository;
import com.block20.repositories.MemberRepository;
import com.block20.services.MemberService;
import java.util.List;
import java.util.UUID;

public class MemberServiceImpl implements MemberService {
    
    private MemberRepository memberRepo;
    private AttendanceRepository attendanceRepo; // NEW dependency

    // Updated Constructor
    public MemberServiceImpl(MemberRepository memberRepo, AttendanceRepository attendanceRepo) {
        this.memberRepo = memberRepo;
        this.attendanceRepo = attendanceRepo;
    }

    @Override
    public Member registerMember(String fullName, String email, String phone, String planType) {
        // ... (Keep your existing register code here) ...
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email!");
        if (memberRepo.findByEmail(email) != null) throw new IllegalArgumentException("Email exists!");
        
        String newId = "M" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Member newMember = new Member(newId, fullName, email, phone, planType);
        memberRepo.save(newMember);
        return newMember;
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepo.findAll();
    }

    // NEW: Check-In Logic
    @Override
    public Attendance checkInMember(String memberId) {
        // 1. Find the member
        // Note: We need to implement findById in Repository, but for now we'll search manually
        Member member = memberRepo.findAll().stream()
                .filter(m -> m.getMemberId().equalsIgnoreCase(memberId))
                .findFirst()
                .orElse(null);

        // 2. Validations
        if (member == null) {
            throw new IllegalArgumentException("Member ID not found: " + memberId);
        }
        if (!"Active".equalsIgnoreCase(member.getStatus())) {
            throw new IllegalStateException("Access Denied: Member is " + member.getStatus());
        }

        // 3. Create Attendance Record
        String visitId = "V" + System.currentTimeMillis();
        Attendance visit = new Attendance(visitId, member.getMemberId(), member.getFullName());
        
        // 4. Save
        attendanceRepo.save(visit);
        return visit;
    }
    @Override
    public void checkOutMember(String memberId) {
        // 1. Find the active visit
        Attendance activeVisit = attendanceRepo.findActiveVisitByMemberId(memberId);
        
        if (activeVisit == null) {
            throw new IllegalStateException("Member is not currently checked in.");
        }

        // 2. Set the checkout time
        activeVisit.setCheckOutTime(java.time.LocalDateTime.now());
        // (Since it's in-memory, we don't strictly need to 'save' again, but it's good practice)
    }

    @Override
    public boolean isMemberCheckedIn(String memberId) {
        return attendanceRepo.findActiveVisitByMemberId(memberId) != null;
    }
    @Override
    public int getCurrentOccupancyCount() {
        return attendanceRepo.countActiveVisits();
    }
}