package com.petlife.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petlife.model.Employee;
import com.petlife.repository.EmployeeRepository;

@Service
public class EmployeeService {
	
	@Autowired
	private EmployeeRepository empRepos;
	
	//新增員工
	public  Employee register(Employee emp  , String plainPassword) {
		String hashed = PasswordUtils.hashPassword(plainPassword);
		emp.setPasswordHash(hashed);
		return empRepos.save(emp);
	}
	
	//查詢員工 by ID
	public Employee findById(Integer empId) {
		return empRepos.findById(empId).orElse(null);
	}
	
	//查詢所有員工
	public List<Employee> findAll(){
		
		return empRepos.findAll();
	}
	
	//模糊查詢姓名
	
	public List<Employee> findByName(String keyword){
		return empRepos.findByEmpNameContaining(keyword);
	}
	
	//更新員工狀態
	public boolean updateStatus(Integer empId, String status) {
		Employee emp = findById(empId);
		if(emp !=null) {
			emp.setStatus(status);
			empRepos.save(emp);
			return true;
		}
		return false;
	}
	
	//登入邏輯
	public String  loginStatus(String username ,String rawPassword) {
		Employee emp = empRepos.findByUsername(username);
		if(emp == null) {
			return "notfound" ; //查無此號
		}
		if("disabled".equals(emp.getStatus())) {
			return "disabled"; //帳號停權
		}
		if(!PasswordUtils.checkPassword(rawPassword, emp.getPasswordHash())) {
			return "wrongpassword"; //密碼錯誤
		}
		
		//登入成功 ->更新最後登入時間
		emp.setLastLoginAt(new Timestamp(System.currentTimeMillis()));
		empRepos.save(emp);
		return "success";
	}
	
	public Employee findByUsername(String username) {
		return empRepos.findByUsername(username);
	}
	
	
	
}
