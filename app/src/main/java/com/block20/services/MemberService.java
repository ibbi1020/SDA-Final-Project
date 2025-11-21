package com.block20.services;

import com.block20.models.Member;
import java.util.List;

public interface MemberService {
    void registerMember(String fullName, String email, String phone, String planType);
    List<Member> getAllMembers();
}