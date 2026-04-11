package com.petlife.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petlife.model.Member;
import com.petlife.repository.IMemberDao;

@Service
public class MemberService implements IMemberService {
	
	@Autowired
	private IMemberDao memberDao; // 依賴介面 不依賴JPA
	
	@Override
	public Member register(Member member) {
		member.setPasswordHash(PasswordUtils.hashPassword(member.getPasswordHash()));
		member.setRegisterTime(LocalDateTime.now());
		member.setAccountStatus("active");
		return memberDao.save(member);
		
	}

	@Override
	public Member login(String email, String password) {
		
		return memberDao.findByEmail(email).filter(m -> 
				PasswordUtils.checkPassword(password, m.getPasswordHash()))
				.map(m ->{
					m.setLastLogin(LocalDateTime.now());
					return memberDao.save(m);
				}).orElse(null);
	}

	@Override
	public Member update(Member member) {
		
		return memberDao.save(member);
	}

	@Override
	public boolean softDelete(Integer memberId) {
		
		return memberDao.softDelete(memberId);
	}
	
	@Override
    public boolean disable(Integer memberId) {
        return memberDao.disable(memberId); // 呼叫 DAO 停權方法
    }

	@Override
	public List<Member> findAll() {
		
		return memberDao.findAll();
	}

	@Override
	public Member findById(Integer memberId) {
		
		return memberDao.findById(memberId).orElse(null);
	}

}
