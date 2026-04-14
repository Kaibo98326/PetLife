package com.petlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petlife.model.EmployeeRole;
import com.petlife.model.EmployeeRoleId;

public interface EmployeeRoleRepository extends JpaRepository<EmployeeRole, EmployeeRoleId> {
	
	//查某些員工的所有腳色
	List<EmployeeRole> findByEmployeeEmpId(Integer empId);
	
	//查某些腳色底下有哪些員工
	List<EmployeeRole> findByRoleRoleId(Integer roleId);
}
