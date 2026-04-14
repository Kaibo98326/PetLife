package com.petlife.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Integer memberId;	//會員編號
	
	@Column(name = "member_name" ,nullable = false)
	private String memberName;	//會員姓名
	
	private String phone;		//電話
	
	@Column(nullable = false ,unique = true)
	private String email;		//登入帳號(e-mail)
	
	@Column(name = "password_hash",nullable = false)
	@JsonIgnore
	private String passwordHash; //雜湊密碼
	
	private String address;
	private String provider;
	
	@Column(name="provider_user_id")
	private String providerUserId;
	
	@Column(name = "account_status")
	private String accountStatus;
	
	@Column(name = "register_time")
	private LocalDateTime registerTime;
	
	@Column(name = "last_login_at")
	private LocalDateTime lastLogin;
	
	// Getter / Setter
    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getProviderUserId() { return providerUserId; }
    public void setProviderUserId(String providerUserId) { this.providerUserId = providerUserId; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public LocalDateTime getRegisterTime() { return registerTime; }
    public void setRegisterTime(LocalDateTime registerTime) { this.registerTime = registerTime; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }


}
