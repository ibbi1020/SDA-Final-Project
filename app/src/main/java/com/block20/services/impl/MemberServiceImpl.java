package com.block20.services.impl;

import com.block20.models.Member;
import com.block20.repositories.MemberRepository;
import com.block20.services.MemberService;
import java.util.List;
import java.util.UUID;

public class MemberServiceImpl implements MemberService {
    
    private MemberRepository memberRepo;

    public MemberServiceImpl(MemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }

    @Override
    public void registerMember(String fullName, String email, String phone, String planType) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address!");
        }
        
        if (memberRepo.findByEmail(email) != null) {
            throw new IllegalArgumentException("Member with this email already exists!");
        }

        String newId = "M" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Member newMember = new Member(newId, fullName, email, phone, planType);
        memberRepo.save(newMember);
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepo.findAll();
    }
}