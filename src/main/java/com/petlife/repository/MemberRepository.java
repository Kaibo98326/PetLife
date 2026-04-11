package com.petlife.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petlife.model.Member;

public interface MemberRepository extends JpaRepository<Member , Integer>{
	
	// 查詢會員 by email
    Optional<Member> findByEmail(String email);

    // 檢查 email 是否存在
    boolean existsByEmail(String email);

    // 檢查 phone 是否存在
    boolean existsByPhone(String phone);


}
