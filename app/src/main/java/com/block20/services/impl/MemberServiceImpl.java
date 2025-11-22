package com.block20.services.impl;

import com.block20.models.Attendance;
import com.block20.models.Member;
import com.block20.models.Transaction; // <--- NEW
import com.block20.models.AuditLog; // <--- NEW
import com.block20.repositories.AttendanceRepository;
import com.block20.repositories.MemberRepository;
import com.block20.repositories.TransactionRepository; // <--- NEW
import com.block20.services.MemberService;
import com.block20.services.AuditService; // <--- NEW
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MemberServiceImpl implements MemberService {
    
    private MemberRepository memberRepo;
    private AttendanceRepository attendanceRepo;
    private TransactionRepository transactionRepo; // <--- NEW
    private AuditService auditService; // <--- NEW

    public MemberServiceImpl(MemberRepository memberRepo, 
                             AttendanceRepository attendanceRepo,
                             TransactionRepository transactionRepo,
                             AuditService auditService) { // <--- Add to constructor
        this.memberRepo = memberRepo;
        this.attendanceRepo = attendanceRepo;
        this.transactionRepo = transactionRepo;
        this.auditService = auditService;
    }
    @Override
    public Member registerMember(String fullName, String email, String phone, String planType) {
        // 1. Validate Syntax
        validateMemberData(fullName, email, phone);

        // 2. Check Duplicates (Business Rule)
        if (memberRepo.findByEmail(email) != null) {
            throw new IllegalArgumentException("Member with this email already exists!");
        }

        String newId = "M" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Member newMember = new Member(newId, fullName, email, phone, planType);
        memberRepo.save(newMember);
        
        // Record Transaction
        double fee = getPlanPrice(planType);
        String txnId = "TXN" + System.currentTimeMillis();
        Transaction txn = new Transaction(txnId, newId, "Enrollment", fee);
        transactionRepo.save(txn);
        
        auditService.logAction(newId, "CREATED", "Member enrolled with plan: " + planType);
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
    @Override
    public List<Attendance> getAllAttendanceRecords() {
        return attendanceRepo.findAll();
    }
    @Override
    public void updateMemberDetails(String id, String name, String email, String phone, String address) {
        Member m = memberRepo.findAll().stream()
            .filter(member -> member.getMemberId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));
            
        // 1. Validate Syntax
        validateMemberData(name, email, phone);
        
        // 2. Check Email Uniqueness (Only if email changed)
        if (!m.getEmail().equalsIgnoreCase(email)) {
            if (memberRepo.findByEmail(email) != null) {
                throw new IllegalArgumentException("Email is already taken by another member.");
            }
        }
        
        // Update fields
        m.setFullName(name);
        m.setEmail(email);
        m.setPhone(phone);
        m.setAddress(address); 
        
        memberRepo.save(m);
        auditService.logAction(id, "PROFILE_UPDATE", "Changed details.");
        System.out.println("Audit: Profile updated for " + id);
    }

    @Override
    public void changeMemberStatus(String id, String newStatus) {
        Member m = memberRepo.findAll().stream()
            .filter(member -> member.getMemberId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));
            
        m.setStatus(newStatus);
        memberRepo.save(m);
        auditService.logAction(id, "STATUS_CHANGE", "Status changed" );
    }

    @Override
    public void deleteMember(String id) {
        // Check for active debt? (Skipping for now, but this is where that logic goes)
        memberRepo.delete(id);
    }
    // --- VALIDATION LOGIC ---
    private void validateMemberData(String name, String email, String phone) {
        // 1. Check Empty Fields
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be empty.");
        }
        
        // 2. Check Email Regex (Standard Pattern)
        // Allows: name@domain.com
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (email == null || !email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format (Example: user@mail.com).");
        }

        // 3. Check Phone Regex
        // Allows: 10 digits (1234567890) or dashes (123-456-7890)
        String phoneRegex = "^\\d{10}|\\d{3}-\\d{3}-\\d{4}|\\d{3}-\\d{4}$";
        if (phone == null || !phone.matches(phoneRegex)) {
            throw new IllegalArgumentException("Invalid phone format (Use 10 digits or 555-0199).");
        }
        
    }
    @Override
    public List<AuditLog> getMemberHistory(String memberId) {
        return auditService.getLogsForMember(memberId);
    }
}