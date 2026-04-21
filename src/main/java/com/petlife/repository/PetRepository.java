	package com.petlife.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.petlife.model.Pet;
import java.util.List;


@Repository
public interface PetRepository extends JpaRepository<Pet, Integer>{
	
	//查詢某會員的所有寵物
	List<Pet> findByMember(Integer memberId);
	
	//依狀態查詢
	List<Pet> findByStatus(String status);
	
	
	//依名字模糊搜尋
	List<Pet> findByPetName(String petName);
	
	// 查某會員的寵物，排除已刪除
    List<Pet> findByMemberMemberIdAndStatusNot(Integer memberId, String status);
    
    Page<Pet> findByPetNameContaining(String keyword , Pageable pageable);
    
    Page<Pet> findByMemberMemberId(Integer memberId, Pageable pageable);
	
}
