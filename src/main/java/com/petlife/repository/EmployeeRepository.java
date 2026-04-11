package com.petlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petlife.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	
	//查詢員工 by username(登入用)
	Employee findByUsername(String username);
	
	//模糊查詢姓名
	List<Employee> findByEmpNameContaining(String keyword);
	
	//查詢員工狀態
	List<Employee> findByStatus(String status);
}
