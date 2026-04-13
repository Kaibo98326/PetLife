package com.petlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petlife.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	//先寫起來的方法，依照名稱找腳色
	Role findByRoleName(String roleName);
}
