package com.petlife.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.petlife.model.Member;
import com.petlife.repository.MemberRepository;

@Service
public class MemberServiceImpl implements IMemService{
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Override
	public Page<Member> findAll(Pageable pageable){
		return memberRepo.findAll(pageable);
	}
	@Override
    public Page<Member> searchByName(String keyword, Pageable pageable) {
        return memberRepo.findByMemberNameContaining(keyword, pageable);
    }
	@Override
    public Member findById(Integer id) {
        return memberRepo.findById(id).orElse(null);
    }

    @Override
    public Member save(Member member) {
        return memberRepo.save(member);
    }

    @Override
    public Member updateMember(Integer id, Member updatedMember) {
        Member member = findById(id);
        if (member == null) {
            return null;
        }
        member.setMemberName(updatedMember.getMemberName());
        member.setEmail(updatedMember.getEmail());
        member.setPhone(updatedMember.getPhone());
        member.setAccountStatus(updatedMember.getAccountStatus());
        return save(member);
    }

    @Override
    public Member changePassword(Integer id, String newPassword) {
        Member member = findById(id);
        if (member == null) {
            return null;
        }
        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        member.setPasswordHash(hashedPassword); // 直接更新，不做認證
        return save(member);
    }
    
    @Override
    public String login(String email, String password) {
        Member member = memberRepo.findByEmail(email).orElse(null);

        if (member == null) {
            return "notfound";
        }
        if ("disabled".equals(member.getAccountStatus())) {
            return "disabled";
        }
        if ("deleted".equals(member.getAccountStatus())) {
            return "deleted";
        }
        if (PasswordUtils.checkPassword(password, member.getPasswordHash())) {
            member.setLastLogin(LocalDateTime.now());
            memberRepo.save(member);
            return "login_success";
        }
        return "wrongpassword";
    }




}
