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
import org.springframework.web.bind.annotation.ResponseBody;

import com.petlife.model.StayRoomType;
import com.petlife.service.StayRoomTypeService;

@Controller
@RequestMapping("/hotel/types")
public class StayRoomTypeController {
	
	// DI from Service
	@Autowired
	private StayRoomTypeService stayRoomTypeService;
	
	@GetMapping
    public String Page() {
        return "hotelTypes";
    }
	
	
	// 點入畫面就嵌入 R 全部資料畫面
	@GetMapping("/data")
	@ResponseBody
	public List<StayRoomType> viewAllStayRoomType(){
		return stayRoomTypeService.findAllStayRoomTypes();
	}
	
	// C 房型
	@PostMapping
	@ResponseBody
	public StayRoomType createStayRoomTypes(@RequestBody StayRoomType stayRoomType){
		return stayRoomTypeService.saveStayRoomType(stayRoomType);
	}
	
	// U 房型
	@PutMapping("/{id}")
	@ResponseBody
	public StayRoomType updataStayRoomType(@PathVariable Integer id,@RequestBody StayRoomType stayRoomType) {
		
		stayRoomType.setRoomTypeId(id);
		
		return stayRoomTypeService.saveStayRoomType(stayRoomType);
		
	}
	
	// D 房型
	@DeleteMapping("/{id}")
	@ResponseBody
	public void deleteStayRoomType(@PathVariable Integer id) {
		
		
		
		stayRoomTypeService.DeleteStayRoomType(id);
	}
	
	
	
}
