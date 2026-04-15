package com.petlife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petlife.model.Role;
import com.petlife.repository.RoleRepository;

@Service
public class RoleService {
	@Autowired
	private RoleRepository roleRepo;
	
	//新增腳色
	public Role createRole(String roleName) {
		Role role = new Role();
		role.setRoleName(roleName);
		return roleRepo.save(role);
	}
	
	//查詢所有腳色
	public List<Role> getAllRoles(){
		return roleRepo.findAll();
	}
	//依照名稱查腳色
	public Role getRoleByName(String roleName) {
		return roleRepo.findByRoleName(roleName);
	}
	//依照ID茶腳色
	public Role getRoleById(Integer roleId) {
		return roleRepo.findById(roleId).orElse(null);
	}
	
	//修改腳色名稱
	public Role updateRole(Integer roleId , String newName) {
		Role role = roleRepo.findById(roleId).orElseThrow(
				() -> new IllegalArgumentException("Role not found: " + roleId)
		 );
		role.setRoleName(newName);
		return roleRepo.save(role);
	}
	
	//刪除腳色
	public void deleteRole(Integer roleId) {
		if(!roleRepo.existsById(roleId)) {
			throw new IllegalArgumentException("Role not found:" + roleId);
		}
		roleRepo.deleteById(roleId);
	}
	
	
	
	
	
}
