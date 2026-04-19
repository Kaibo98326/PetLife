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
	public String register(Member member) {
		// 檢查 Email 是否存在
	    Member existingByEmail = memberDao.findByEmail(member.getEmail()).orElse(null);
	    if (existingByEmail != null) {
	        return "register_duplicate"; // email 已存在
	    }

	    // 檢查 Phone 是否存在
	    List<Member> allMembers = memberDao.findAll();
	    boolean phoneExists = allMembers.stream()
	                                    .anyMatch(m -> m.getPhone() != null && m.getPhone().equals(member.getPhone()));
	    if (phoneExists) {
	        return "register_duplicate"; // phone 已存在
	    }

	    // 新增會員
	    member.setPasswordHash(PasswordUtils.hashPassword(member.getPasswordHash()));
	    member.setRegisterTime(LocalDateTime.now());
	    member.setAccountStatus("active");
	    memberDao.save(member);

	    return "register_success";

		
	}

	@Override
	public String login(String email, String password) {
	    Member member = memberDao.findByEmail(email).orElse(null);

	    if (member == null) {
	        return "notfound";
	    }
	    if ("disable".equals(member.getAccountStatus())) {
	        return "disabled";
	    }
	    if ("delete".equals(member.getAccountStatus())) {
	        return "deleted";
	    }
	    if (PasswordUtils.checkPassword(password, member.getPasswordHash())) {
	        member.setLastLogin(LocalDateTime.now());
	        memberDao.save(member);
	        return "login_success";
	    }
	    return "wrongpassword";
	}



	@Override
	public String updateMember(Integer id, Member updatedMember) {
	    Member dbMember = memberDao.findById(id).orElse(null);
	    if (dbMember == null) {
	        return "notfound";
	    }

	    // 檢查 Email 是否重複（排除自己）
	    Member existingByEmail = memberDao.findByEmail(updatedMember.getEmail()).orElse(null);
	    if (existingByEmail != null && !existingByEmail.getMemberId().equals(id)) {
	        return "duplicate_email";
	    }

	    // 檢查 Phone 是否重複（排除自己）
	    List<Member> allMembers = memberDao.findAll();
	    boolean phoneExists = allMembers.stream()
	        .anyMatch(m -> m.getPhone() != null 
	                    && m.getPhone().equals(updatedMember.getPhone()) 
	                    && !m.getMemberId().equals(id));
	    if (phoneExists) {
	        return "duplicate_phone";
	    }

	    // 更新允許修改的欄位
	    dbMember.setMemberName(updatedMember.getMemberName());
	    dbMember.setEmail(updatedMember.getEmail());
	    dbMember.setPhone(updatedMember.getPhone());
	    dbMember.setAddress(updatedMember.getAddress());

	    // 如果有密碼欄位，記得加密處理
	    if (updatedMember.getPasswordHash() != null && !updatedMember.getPasswordHash().isEmpty()) {
	        dbMember.setPasswordHash(PasswordUtils.hashPassword(updatedMember.getPasswordHash()));
	    }

	    memberDao.save(dbMember);
	    return "update_success";
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
	
	@Override
	public Member findByEmail(String Email) {
		
		return memberDao.findByEmail(Email).orElse(null);
	}

}
