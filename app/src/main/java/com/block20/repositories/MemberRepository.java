package com.block20.repositories;

import com.block20.models.Member;
import java.util.List;

public interface MemberRepository {
    void save(Member member);
    Member findByEmail(String email);
    List<Member> findAll();
    void delete(String memberId);
}