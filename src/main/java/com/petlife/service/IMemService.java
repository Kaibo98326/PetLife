package com.petlife.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.petlife.model.Member;

public interface IMemService {
	Page<Member> findAll(Pageable pageable);
    Page<Member> searchByName(String keyword, Pageable pageable);
    
    //更新會員資料
    Member updateMember(Integer id , Member updatedMember);
    //更改密碼
    Member changePassword(Integer id , String newPassword);
    //伊ID 查詢會員
    Member findById(Integer id);
    //儲存會員
    Member save(Member member);
    
	String login(String email, String password);
}
