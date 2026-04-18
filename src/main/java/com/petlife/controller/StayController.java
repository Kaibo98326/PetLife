package com.petlife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.petlife.model.Stay;
import com.petlife.service.StayService;

@Controller
@RequestMapping("hotel/orders")
public class StayController {

	//DI
	@Autowired
	private StayService stayService;
	
	//網址
	@GetMapping
	public String page() {
		return "hotelOrders";
	}
	
	// R 全部
	@GetMapping("/data")
	@ResponseBody
	public List<Stay> viewAllStays(){
		return stayService.findAllStays();
	}
	
	// R 單筆資料
	@GetMapping("/{id}")
	@ResponseBody
	public Stay viewStay(@PathVariable Integer id) {
		return stayService.findStay(id);
	}
	
	// C 
	@PostMapping
	@ResponseBody
	public Stay createStay(@RequestBody Stay stay) {
		return stayService.createStay(stay);
	}

	// U
	@PutMapping("/{id}")
	@ResponseBody
	public Stay updataStay(@PathVariable Integer id,@RequestBody Stay stay) {
		stay.setStayId(id);
		return stayService.saveStay(stay);
	}
	
	//D soft delete 
	@DeleteMapping("/{id}")
	@ResponseBody
	public void deleteStay(@PathVariable Integer id) {
		stayService.deleteStay(id);
	}
	
	@GetMapping("/search")
	@ResponseBody
	public List<Stay> searchByPhone(@RequestParam String phone) {
	    return stayService.findByPhone(phone);
	}
}
