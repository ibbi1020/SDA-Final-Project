package com.block20.repositories.impl;

import com.block20.models.Member;
import com.block20.repositories.MemberRepository;
import java.util.ArrayList;
import java.util.List;

public class MemberRepositoryImpl implements MemberRepository {
    
    // In-Memory Database
    private List<Member> memberTable = new ArrayList<>();

    @Override
    public void save(Member member) {
        // 1. Check if this member is already in the list
        boolean exists = false;
        for (int i = 0; i < memberTable.size(); i++) {
            if (memberTable.get(i).getMemberId().equals(member.getMemberId())) {
                // Found it! Update the existing record
                memberTable.set(i, member);
                exists = true;
                System.out.println("Repo: Updated existing member " + member.getFullName());
                break;
            }
        }

        // 2. If not found, add it as new
        if (!exists) {
            memberTable.add(member);
            System.out.println("Repo: Saved new member " + member.getFullName());
        }
    }

    @Override
    public Member findByEmail(String email) {
        for (Member m : memberTable) {
            if (m.getEmail().equalsIgnoreCase(email)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(memberTable);
    }
    @Override
    public void delete(String memberId) {
        memberTable.removeIf(m -> m.getMemberId().equals(memberId));
        System.out.println("Repo: Deleted member " + memberId);
    }
}