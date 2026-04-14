package com.petlife.model;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity @Table(name =  "Employee")
@Component
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Employee {
	
	@Id @Column(name = "emp_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer empId;				//員工編號
	
	@NonNull
	@Column(name = "username" , nullable = false , unique = true , length = 50)
	private String username;				//登入帳號
	
	@NonNull @Column(name = "password_hash" ,nullable = false ,length = 255)
	private String passwordHash;			//雜湊密碼
	
	@NonNull @Column(name = "emp_name", nullable = false , length = 100)
	private String empName;				//員工姓名
	
	@Column(name = "emp_phone" , length = 20)
	private String empPhone;				//員工電話
	
	@Column(name = "emp_address" ,length = 255)
	private String empAddress;			//員工地址
	
	@Column(name = "emergency_contact" , length = 50)
	private String emergencyContact;		//緊急聯絡人
	
	@Column(name = "emergency_phone",length = 50 )
	private String emergencyphone;
	
	@Column(name = "status" , length = 20)
	private String status = "active";		//帳號狀態
	
	@Column(name = "last_login")
	private Timestamp lastLoginAt;		//最後登入時間
	
}
