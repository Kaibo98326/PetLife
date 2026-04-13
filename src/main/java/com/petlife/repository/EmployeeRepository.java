package com.petlife.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petlife.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	
	//分頁模糊查詢姓名
	Page<Employee> findByEmpNameContaining(String keyword , Pageable pageable);
	
	//查詢員工 by username(登入用)
	Employee findByUsername(String username);
	
	List<Employee> findByEmpName(String empName);
	
	
	//查詢員工狀態
	List<Employee> findByStatus(String status);
}
