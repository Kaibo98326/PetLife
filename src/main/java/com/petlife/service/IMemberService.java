package com.petlife.service;

import java.util.List;

import com.petlife.model.Member;

public interface IMemberService {
	
	// 註冊會員
    Member register(Member member);

    // 登入會員
    Member login(String email, String password);

    // 更新會員資料
    Member update(Member member);

    // 軟刪除會員
    boolean softDelete(Integer memberId);
    
    // 停權會員
    boolean disable(Integer memberId);   


    // 查詢所有會員
    List<Member> findAll();

    // 查詢單一會員
    Member findById(Integer memberId);



}