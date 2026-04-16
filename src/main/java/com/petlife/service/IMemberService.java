package com.petlife.service;

import java.util.List;

import com.petlife.model.Member;

public interface IMemberService {
	
	// 註冊會員
    String  register(Member member);

    // 登入會員
    String login(String email, String password);

    // 更新會員資料
    String updateMember(Integer id, Member updatedMember);


    // 軟刪除會員
    boolean softDelete(Integer memberId);
    
    // 停權會員
    boolean disable(Integer memberId);   


    // 查詢所有會員
    List<Member> findAll();

    // 查詢單一會員
    Member findById(Integer memberId);

    Member findByEmail(String  email);

}