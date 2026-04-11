package com.petlife.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.petlife.model.Employee;
import com.petlife.service.EmployeeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
	
	@Autowired
	private EmployeeService empService;
	
	//顯是登入頁面
	@GetMapping("/loginEmp")
	public String showLoginPage() {
		return "loginEmp"; 
	}
	
	//登入
	@PostMapping("/login")
	public String login(@RequestParam String username,
						@RequestParam String password,
						HttpSession session,
						Model m) {
		String status = empService.loginStatus(username, password);
		
		if("success".equals(status)) {
			Employee emp = empService.findByUsername(username);
			session.setAttribute("empId", emp.getEmpId());
			session.setAttribute("empName", emp.getEmpName());
			m.addAttribute("status" , "success");
		}else {
			m.addAttribute("error",status); //not found / disable / wrongpassword
		}
		return "result2";
		
		
	}
	//登入成功後的會員中心
	@GetMapping("/center")
	public String employeeCenter(HttpSession session , Model m ) {
		Object empId = session.getAttribute("empId");
		Object empName = session.getAttribute("empName");
		
		if(empId == null) {
			return "redirect:/employee/loginEmp";
		}
		
		m.addAttribute("empId", empId);
		m.addAttribute("empName" , empName);
		return "empCenter";
	}
	
	
	//登出
	@GetMapping("/logout")
	public String logout(HttpSession session , Model m) {
		session.invalidate();
		m.addAttribute("status" , "logout");
		return "result2";
	}
	
	@GetMapping("EMO")
	public String EmpOptions() {
		return "EMO";
	}
	
	
	
}
