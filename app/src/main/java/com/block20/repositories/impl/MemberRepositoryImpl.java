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
        memberTable.add(member);
        System.out.println("Repo: Saved member " + member.getFullName());
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
}