package com.petlife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.petlife.model.Employee;
import com.petlife.model.EmployeeRole;
import com.petlife.model.Role;
import com.petlife.repository.EmployeeRepository;
import com.petlife.repository.RoleRepository;
import com.petlife.service.EmployeeRoleService;
import com.petlife.service.RoleService;

@Controller
@RequestMapping("/role")
public class RoleController {
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private EmployeeRoleService employeeRoleService;
	
	@Autowired
	private EmployeeRepository employeeRepo;
	@Autowired
	private RoleRepository roleRepo;
	
	/**
	 * 腳色管理
	 * 
	 * **/
	//查詢腳色共同方法
	private void loadRoles(Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
    }

	
	// 顯示角色清單頁面
    @GetMapping("/list")
    public String listRoles(Model model) {
        loadRoles(model);
        return "roleList :: listFragment";
    }

    // 新增角色
    @PostMapping("/create")
    public String createRole(@RequestParam String roleName, Model model) {
        roleService.createRole(roleName);
        loadRoles(model);
        return "roleList :: listFragment";
    }

    // 修改角色
    @PostMapping("/edit")
    public String updateRole(Role role, Model model) {
        roleService.updateRole(role.getRoleId(), role.getRoleName());
        loadRoles(model);
        return "roleList :: listFragment";
    }

    // 刪除角色
    @PostMapping("/delete")
    public String deleteRole(@RequestParam Integer roleId, Model model) {
        roleService.deleteRole(roleId);
        loadRoles(model);
        return "roleList :: listFragment";
    }

    
    /**
     * 
     * 員工腳色指派
     * 
     * **/
    
    @GetMapping("/empRoleList")
    public String showEmpRoleList(Model model) {
        List<Employee> employees = employeeRepo.findAll();
        List<Role> roles = roleService.getAllRoles();
        List<EmployeeRole> empRoles = employeeRoleService.findAll(); // 查全部綁定

        model.addAttribute("employees", employees);
        model.addAttribute("roles", roles);
        model.addAttribute("empRoles", empRoles);

        return "employeeRole";
    }


    
    @GetMapping("/employeeRoles")
    public String listEmployeeRoles(@RequestParam Integer empId, Model model) {
        Employee emp = employeeRepo.findById(empId).orElseThrow();
        List<EmployeeRole> empRoles = employeeRoleService.getRolesByEmployee(empId);
        List<Employee> employees = employeeRepo.findAll();
        List<Role> roles = roleService.getAllRoles();

        model.addAttribute("employee", emp);
        model.addAttribute("empRoles", empRoles);
        model.addAttribute("employees", employees);
        model.addAttribute("roles", roles);

        return "employeeRole :: roleBindFragment";
    }

    @PostMapping("/assign")
    public String assignRole(@RequestParam Integer empId,
                             @RequestParam Integer roleId,
                             Model model) {
        Employee emp = employeeRepo.findById(empId).orElseThrow();
        Role role = roleRepo.findById(roleId).orElseThrow();
        employeeRoleService.assignRoleToEmp(emp, role);

        // 這裡改成查全部
        List<EmployeeRole> empRoles = employeeRoleService.findAll();
        model.addAttribute("empRoles", empRoles);
        model.addAttribute("employees", employeeRepo.findAll());
        model.addAttribute("roles", roleRepo.findAll());

        return "employeeRole :: roleBindFragment";
    }



    @PostMapping("/remove")
    public String removeRole(@RequestParam Integer empId, @RequestParam Integer roleId, Model model) {
        employeeRoleService.removeRoleFromEmployee(empId, roleId);
        
     // 這裡改成查全部
        List<EmployeeRole> empRoles = employeeRoleService.findAll();
        model.addAttribute("empRoles", empRoles);
        model.addAttribute("employees", employeeRepo.findAll());
        model.addAttribute("roles", roleRepo.findAll());
        
        return "employeeRole :: roleBindFragment";
    }



	
	
	
	
}
