package com.block20.services.impl;

import com.block20.models.Attendance;
import com.block20.models.Member;
import com.block20.models.Transaction; // <--- NEW
import com.block20.repositories.AttendanceRepository;
import com.block20.repositories.MemberRepository;
import com.block20.repositories.TransactionRepository; // <--- NEW
import com.block20.services.MemberService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MemberServiceImpl implements MemberService {
    
    private MemberRepository memberRepo;
    private AttendanceRepository attendanceRepo;
    private TransactionRepository transactionRepo; // <--- NEW

    // UPDATED CONSTRUCTOR: Now takes 3 repositories
    public MemberServiceImpl(MemberRepository memberRepo, 
                             AttendanceRepository attendanceRepo,
                             TransactionRepository transactionRepo) {
        this.memberRepo = memberRepo;
        this.attendanceRepo = attendanceRepo;
        this.transactionRepo = transactionRepo;
    }

    @Override
    public Member registerMember(String fullName, String email, String phone, String planType) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email!");
        if (memberRepo.findByEmail(email) != null) throw new IllegalArgumentException("Member with this email already exists!");

        String newId = "M" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Member newMember = new Member(newId, fullName, email, phone, planType);
        memberRepo.save(newMember);
        
        // --- NEW: RECORD PAYMENT ---
        double fee = getPlanPrice(planType);
        String txnId = "TXN" + System.currentTimeMillis();
        Transaction txn = new Transaction(txnId, newId, "Enrollment", fee);
        transactionRepo.save(txn);
        // ---------------------------
        
        return newMember;
    }

    @Override
    public void renewMembership(String memberId, String newPlanType) {
        Member member = memberRepo.findAll().stream()
            .filter(m -> m.getMemberId().equals(memberId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.setPlanType(newPlanType);
        member.setStatus("Active");
        
        if (member.getExpiryDate().isBefore(LocalDate.now())) {
            member.setExpiryDate(LocalDate.now().plusMonths(1));
        } else {
            member.setExpiryDate(member.getExpiryDate().plusMonths(1));
        }
        memberRepo.save(member);
        
        // --- NEW: RECORD PAYMENT ---
        double fee = getPlanPrice(newPlanType);
        String txnId = "TXN" + System.currentTimeMillis();
        Transaction txn = new Transaction(txnId, memberId, "Renewal", fee);
        transactionRepo.save(txn);
        // ---------------------------
        
        System.out.println("Renewed: " + member.getFullName());
    }

    // Helper to get price (Matches your UI logic)
    private double getPlanPrice(String plan) {
        if (plan == null) return 0.0;
        return switch (plan) {
            case "Basic" -> 29.99;
            case "Premium" -> 49.99;
            case "Elite" -> 79.99;
            case "Student" -> 24.99;
            default -> 29.99;
        };
    }

    // ... (Keep checkInMember, checkOutMember, etc. exactly the same as before) ...
    @Override
    public List<Member> getAllMembers() { return memberRepo.findAll(); }

    @Override
    public Attendance checkInMember(String memberId) {
        Member member = memberRepo.findAll().stream()
                .filter(m -> m.getMemberId().equalsIgnoreCase(memberId))
                .findFirst()
                .orElse(null);

        if (member == null) throw new IllegalArgumentException("Member ID not found: " + memberId);
        if (!"Active".equalsIgnoreCase(member.getStatus())) throw new IllegalStateException("Access Denied: Member is " + member.getStatus());

        String visitId = "V" + System.currentTimeMillis();
        Attendance visit = new Attendance(visitId, member.getMemberId(), member.getFullName());
        attendanceRepo.save(visit);
        return visit;
    }

    @Override
    public void checkOutMember(String memberId) {
        Attendance activeVisit = attendanceRepo.findActiveVisitByMemberId(memberId);
        if (activeVisit == null) throw new IllegalStateException("Member is not currently checked in.");
        activeVisit.setCheckOutTime(java.time.LocalDateTime.now());
    }

    @Override
    public boolean isMemberCheckedIn(String memberId) {
        return attendanceRepo.findActiveVisitByMemberId(memberId) != null;
    }

    @Override
    public int getCurrentOccupancyCount() {
        return attendanceRepo.countActiveVisits();
    }
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }
}