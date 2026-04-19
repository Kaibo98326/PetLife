package com.petlife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.petlife.model.Pet;
import com.petlife.repository.PetRepository;

@Service
public class PetService {

	// 依賴注入 (DI)
	@Autowired
	private PetRepository petRepository;
	
	// C & D寵物 (在 JPA 中，只要有帶 ID 就是更新，沒帶 ID 就是新增)
	public Pet savePet(Pet pet) {
		return petRepository.save(pet); 
	}
	
	// 單筆ID查詢 寵物
	public Pet findPet(Integer petId) {
		// 因為 JPA 回傳的是 Optional<Pet> ，JAVA要預設找不到的情況
		// 所以強制你增加orElse(null)
		return petRepository.findById(petId).orElse(null);
	}
	
	// 查詢全部寵物
	public List<Pet> findAllPets() {
		return petRepository.findAll();
	}
	
	// 用 ID 刪除寵物
	public void deletePet(Integer petId) {
		petRepository.deleteById(petId);
	}
	
	//軟刪除寵物(修改狀態攔)
	public boolean softDeletePet(Integer petId) {
		Pet pet = petRepository.findById(petId).orElse(null);
		if(pet != null) {
			pet.setStatus("delete");
			petRepository.save(pet);
			return true;
		}
		return false;
	}
	
	//查詢某會員的所有寵物
	public List<Pet> findPetsByMemberId(Integer memberId){
		return petRepository.findByMember(memberId);
	}
	
	//依狀態查寵物
	public List<Pet> findPetsByStatus(String status){
		return petRepository.findByStatus(status);
	}
	
	//模糊搜尋寵物名稱
	public Page<Pet> searchPetsByName(String keyword ,int page , int size){
		Pageable pageable = PageRequest.of(page, size);
		
		return petRepository.findByPetNameContaining(keyword , pageable); 
	}
	
	//查詢主人ID的寵物(分頁)
	public Page<Pet> findPetsByMemberId(Integer memberId, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    return petRepository.findByMemberMemberId(memberId, pageable);
	}


	
	//查詢某會員的所有有效寵物(排除軟刪除)
	public List<Pet> findActivePetsByMemberId(Integer memberId){
		return petRepository.findByMemberMemberIdAndStatusNot(memberId, "delete");
		
	}
	
	// 會員端軟刪除寵物
	public boolean softDeletePetByMember(Integer petId, Integer memberId) {
		Pet pet = petRepository.findById(petId).orElse(null);
		if(pet != null && pet.getMember().getMemberId().equals(memberId)) {
			pet.setStatus("delete");
			petRepository.save(pet);
			return true;
		}
		return false;
	}
	
	
	//後端分頁查詢所有寵物
	public Page<Pet> getAllPets(int page , int size){
		Pageable pageable = PageRequest.of(page, size);
		return petRepository.findAll(pageable);
	}

	

	
	
	
}

