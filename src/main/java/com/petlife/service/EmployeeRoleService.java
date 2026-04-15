package com.petlife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petlife.model.Employee;
import com.petlife.model.EmployeeRole;
import com.petlife.model.EmployeeRoleId;
import com.petlife.model.Role;
import com.petlife.repository.EmployeeRoleRepository;

@Service
public class EmployeeRoleService {
	
	@Autowired
	private EmployeeRoleRepository empRoleRepo;
	
	//指派腳色給員工
	public EmployeeRole assignRoleToEmp(Employee employee , Role role) {
		EmployeeRoleId id = new EmployeeRoleId(employee.getEmpId(),role.getRoleId());
		EmployeeRole employeeRole = new EmployeeRole(id , employee ,role);
		return empRoleRepo.save(employeeRole);
	}
	
	//查某員工的所有腳色
	public List<EmployeeRole> getRolesByEmployee(Integer empId){
		return empRoleRepo.findByEmployeeEmpId(empId);
	}
	
	//查某腳色底下所有員工
	public List<EmployeeRole> getEmployeeByRole(Integer roleId){
		return empRoleRepo.findByRoleRoleId(roleId);
	}
	
	//移除員工的某個腳色
	public void removeRoleFromEmployee(Integer empId , Integer roleId) {
		EmployeeRoleId id = new EmployeeRoleId(empId , roleId);
		empRoleRepo.deleteById(id);
	}
	// 查所有員工角色綁定
	public List<EmployeeRole> findAll() {
	    return empRoleRepo.findAll();
	}


	
	
	
	
	
	
	
	
}
