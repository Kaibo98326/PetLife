package com.petlife.repository;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.petlife.model.Member;

@Repository
public class MemberDao implements IMemberDao {
	
	 	@Autowired
	    private MemberRepository memberRepository; // JPA Repository

	 	@Override
	    public Optional<Member> findById(Integer memberId) {
	        return memberRepository.findById(memberId);
	    }

	    @Override
	    public Optional<Member> findByEmail(String email) {
	        return memberRepository.findByEmail(email);
	    }

	    @Override
	    public List<Member> findAll() {
	        return memberRepository.findAll();
	    }

	    @Override
	    public Member save(Member member) {
	        return memberRepository.save(member);
	    }

	    @Override
	    public boolean softDelete(Integer memberId) {
	        return memberRepository.findById(memberId).map(m -> {
	            m.setAccountStatus("deleted"); // 軟刪除 → 改狀態欄位
	            memberRepository.save(m);
	            return true;
	        }).orElse(false);
	    }
	    

	    @Override
	    public boolean disable(Integer memberId) {
	        return memberRepository.findById(memberId).map(m -> {
	            m.setAccountStatus("disabled"); // 停權：改狀態
	            memberRepository.save(m);
	            return true;
	        }).orElse(false);
	    }


}
