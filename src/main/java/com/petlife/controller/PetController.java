package com.petlife.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.petlife.model.Member;
import com.petlife.model.Pet;
import com.petlife.service.PetService;



@Controller
@RequestMapping("pets")
public class PetController {
	
	@Autowired
    private PetService petService;

    // 會員端：查詢自己的寵物清單（排除已刪除）
    @GetMapping("/list/{memberId}")
    public String listPets(@PathVariable Integer memberId, Model model) {
    	
        List<Pet> pets = petService.findActivePetsByMemberId(memberId);
        model.addAttribute("pets", pets);
        
        return "petlist"; // 對應 Thymeleaf 頁面
        
    }

    @PostMapping("/add")
	public String addPet(@ModelAttribute Pet pet,
						@RequestParam("file")  MultipartFile file,
						@RequestParam("memberId") Integer memberId) throws IOException {
		
		if(!file.isEmpty()) {
			
			String uploadDir = "C:/uploads/images/pets/";
			String fileName = UUID.randomUUID() +"_" + file.getOriginalFilename();
			
			Path path = Paths.get(uploadDir + fileName);
			Files.createDirectories(path.getParent());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			pet.setPetPhoto("images/pets/" + fileName);
		}
		
		pet.setStatus("active");
		Member member = new Member();
		member.setMemberId(memberId);
		pet.setMember(member);
		
		petService.savePet(pet);
		return "redirect:/pets/list/" + memberId;
		
	}
    
	//載入新增寵物頁面
	@GetMapping("/addForm/{memberId}")
	public String showAddForm(@PathVariable Integer memberId, Model model) {
	    model.addAttribute("memberId", memberId);
	    return "addpetForm";
	}
	//載入修改寵物頁面
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Integer id, Model model) {
	    Pet pet = petService.findPet(id);
	    model.addAttribute("pet", pet);
	    model.addAttribute("memberId", pet.getMember().getMemberId());
	    return "editpetForm"; // 對應 Thymeleaf 模板
	}



	
	//修改寵物資料
	@PostMapping("/update/{id}")
	public String updatePet(@PathVariable Integer id,
							@ModelAttribute Pet pet,
							@RequestParam("file") MultipartFile file,
							@RequestParam("memberId") Integer memberId) throws IOException{
		
		Pet existPet = petService.findPet(id);
		if(existPet != null && existPet.getMember().getMemberId().equals(memberId)) {
			existPet.setPetName(pet.getPetName());
			existPet.setBreed(pet.getBreed());
			existPet.setSpecies(pet.getSpecies());
			existPet.setAge(pet.getAge());
			existPet.setWeight(pet.getWeight());
			existPet.setMedicalHistory(pet.getMedicalHistory());
			
			if(!file.isEmpty()) {
				
				//先刪除舊檔案
        			if(existPet.getPetPhoto() != null) {
        				Path oldPath = Paths.get("C:/uploads/" + existPet.getPetPhoto());
        				Files.deleteIfExists(oldPath);
        			}
				
				String uploadDir = "C:/uploads/images/pets/";
				String fileName = UUID.randomUUID() +"_" + file.getOriginalFilename();
				Path path = Paths.get(uploadDir + fileName);
				Files.createDirectories(path.getParent());
				Files.copy(file.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);
				
				existPet.setPetPhoto("images/pets/" + fileName);
			}
			
			petService.savePet(existPet);
			
		}
		return  "redirect:/pets/list/" + memberId ;
		
	}
	
	
	//軟刪除寵物(會員端)
	@PostMapping("/delete/{id}")
	public ResponseEntity<?> deletePet(@PathVariable Integer id,
										@RequestParam("memberId") Integer memberId){
		
		boolean success = petService.softDeletePetByMember(id, memberId);
		if(success) {
			return ResponseEntity.ok("寵物已刪除");
		}
		return ResponseEntity.badRequest().body("刪除失敗");
	}
	
	//員工端用搜尋所有寵物
	// 寵物清單 (分頁)
    @GetMapping("/admin/list")
    public String petList(@RequestParam(defaultValue = "0") int page, Model m) {
        int pageSize = 10;
        Page<Pet> petPage = petService.getAllPets(page, pageSize);

        m.addAttribute("petList", petPage.getContent());
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", petPage.getTotalPages());

        return "adminPetList :: listFragment";
    }


    
    // 模糊查詢寵物名稱
    @GetMapping("/admin/search")
    public String searchPetByName(@RequestParam(value = "petName", required = false) String name,
                                  @RequestParam(defaultValue = "0") int page,
                                  Model m) {
        if (name == null || name.isBlank()) {
            return petList(page, m);
        }

        int safePage = page < 0 ? 0 : page;
        int pageSize = 10;

        Page<Pet> petPage = petService.searchPetsByName(name, safePage, pageSize);

        m.addAttribute("petList", petPage.getContent());
        m.addAttribute("currentPage", safePage);
        m.addAttribute("totalPages", petPage.getTotalPages());
        m.addAttribute("searchKeyword", name);

        return "adminPetList :: listFragment";
    }
    
    // 查主人 ID 的寵物
    @GetMapping("/admin/searchByMember")
    public String searchPetByMember(@RequestParam(value = "memberId", required = false) Integer memberId,
                                    @RequestParam(defaultValue = "0") int page,
                                    Model m) {
        if (memberId == null) {
            return petList(page, m);
        }

        int safePage = page < 0 ? 0 : page;
        int pageSize = 10;

        Page<Pet> petPage = petService.findPetsByMemberId(memberId, safePage, pageSize);

        m.addAttribute("petList", petPage.getContent());
        m.addAttribute("currentPage", safePage);
        m.addAttribute("totalPages", petPage.getTotalPages());
        m.addAttribute("searchMemberId", memberId);

        return "adminPetList :: listFragment";
    }

	//後台刪除寵物(軟刪除)
    @PostMapping("/admin/delete/{id}")
    public ResponseEntity<?> adminDeletePet(@PathVariable Integer id){
    	 	Pet pet = petService.findPet(id);
    	 	if(pet != null) {
    	 		pet.setStatus("delete");
    	 		petService.savePet(pet);
    	 		return ResponseEntity.ok("寵物已標記為刪除");
    	 	}
    	 	
    	 	return ResponseEntity.badRequest().body("刪除失敗，寵物不存在");
    }
    
    //後端用寵物編輯表單
    @GetMapping("/admin/edit/{id}")
    public String adminshowEditForm(@PathVariable Integer id , Model m ) {
    		Pet pet = petService.findPet(id);
    		m.addAttribute("pet" , pet);
    		m.addAttribute("memberId" , pet.getMember().getMemberId());
    		
    		return "adminEditPetForm";
    }
    
    //後端用寵物修改方法
    @PostMapping("/admin/update/{id}")
    public String adminUpdatePet(@PathVariable Integer id,
                                 @ModelAttribute Pet pet,
                                 @RequestParam(value = "file", required = false) MultipartFile file,
                                 @RequestParam("memberId") Integer memberId,
                                 Model model) {
        try {
            Pet existPet = petService.findPet(id);
            if (existPet != null) {
                existPet.setPetName(pet.getPetName());
                existPet.setBreed(pet.getBreed());
                existPet.setSpecies(pet.getSpecies());
                existPet.setAge(pet.getAge());
                existPet.setWeight(pet.getWeight());
                existPet.setMedicalHistory(pet.getMedicalHistory());
                existPet.setStatus(pet.getStatus());

                if (file != null && !file.isEmpty()) {
                	
                		//先刪除舊檔案
        				if(existPet.getPetPhoto() != null) {
        					Path oldPath = Paths.get("C:/uploads/" + existPet.getPetPhoto());
        					Files.deleteIfExists(oldPath);
        				}
                		
                    String uploadDir = "C:/uploads/images/pets/";
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path path = Paths.get(uploadDir + fileName);
                    Files.createDirectories(path.getParent());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                    existPet.setPetPhoto("images/pets/" + fileName);
                }

                petService.savePet(existPet);
            }
            // 更新後重新查詢列表資料
            Page<Pet> petPage = petService.getAllPets(0, 10);
            model.addAttribute("petList", petPage.getContent());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", petPage.getTotalPages());

            // ✅ 回傳 fragment，不是 redirect
            return "adminPetList :: listFragment";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/pets/admin/list?error=true";
        }
    }

	

	
}
