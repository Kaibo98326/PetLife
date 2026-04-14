package com.petlife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
}

