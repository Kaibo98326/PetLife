package com.petlife.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.petlife.model.Employee;
import com.petlife.service.EmployeeService;
import com.petlife.service.PasswordUtils;

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
	
	//員工清單
	@GetMapping("/list")
	public String empList(@RequestParam(defaultValue = "0") int page,Model m) {
		int pageSize = 10;
		Page<Employee> empPage = empService.findAll(PageRequest.of(page, pageSize));
		m.addAttribute("empList" , empPage.getContent());
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",empPage.getTotalPages());
		return "emplist :: listFragment";
	}
	//模糊查詢姓名
	@GetMapping("/search")
	public String searchEmpByName(@RequestParam(value="empName", required=false) String name,
	                              @RequestParam(defaultValue = "0") int page,
	                              Model m) {
	    // 空字串或 null → 回到一般清單
	    if (name == null || name.isBlank()) {
	        return empList(page, m);
	    }

	    // page 保護，避免負數
	    int safePage = page < 0 ? 0 : page;
	    int pageSize = 10;

	    Page<Employee> empPage = empService.findByNameContaining(name, PageRequest.of(safePage, pageSize));

	    m.addAttribute("empList", empPage.getContent());
	    m.addAttribute("currentPage", safePage);
	    m.addAttribute("totalPages", empPage.getTotalPages());
	    m.addAttribute("searchKeyword", name);
	    System.out.println(">>> 搜尋關鍵字: " + name + ", page=" + page);
	    return "emplist :: listFragment";
	}



	//導到empadd
	@GetMapping("/add")
	public String showAddForm(Model m) {
		return "empAdd :: addFragment"; 
	}
	
	//新增員工
	@PostMapping("/add")
	public String addEmpolyee(@ModelAttribute Employee emp,Model m,
								@RequestParam(defaultValue = "0") int page) {
		empService.register(emp ,emp.getPasswordHash());
		int pageSize = 10;
	    Page<Employee> empPage = empService.findAll(PageRequest.of(page, pageSize));
	    m.addAttribute("empList", empPage.getContent());
	    m.addAttribute("currentPage", page);
	    m.addAttribute("totalPages", empPage.getTotalPages());


		return "emplist :: listFragment";
	}
	
	//導到empedit並帶emp.empId
	@GetMapping("/edit")
	public String showEditForm(@RequestParam("empId") Integer id , Model m) {
		Employee emp = empService.findById(id);
		m.addAttribute("emp", emp);
		return "empEdit :: editFragment";
	}
	
	//送出更新
	@PostMapping("/edit")
	public String updateEmp(Employee emp , Model m,
							@RequestParam(defaultValue = "0") int page) {
		Employee dbemp = empService.findById(emp.getEmpId());
		 if(emp.getPasswordHash() == null || emp.getPasswordHash().isBlank()) {
			 emp.setPasswordHash(dbemp.getPasswordHash());
		 }else {
			emp.setPasswordHash(PasswordUtils.hashPassword(emp.getPasswordHash()));
		}
		 empService.update(emp);
		 int pageSize = 10;
		    Page<Employee> empPage = empService.findAll(PageRequest.of(page, pageSize));
		    m.addAttribute("empList", empPage.getContent());
		    m.addAttribute("currentPage", page);
		    m.addAttribute("totalPages", empPage.getTotalPages());
		    
		 return "emplist :: listFragment";
	}
	
	
	
	
}
