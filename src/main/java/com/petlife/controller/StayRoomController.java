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

import com.petlife.model.StayRoom;
import com.petlife.service.StayRoomService;

@Controller
@RequestMapping("/hotel/rooms")
public class StayRoomController {

	// DI
	@Autowired
	private  StayRoomService stayRoomService;
	
	//網址
	@GetMapping
	public String Page() {
		return "hotelRooms";
	}
	
	// R 全部資料 
	@GetMapping("/data")
	@ResponseBody
	public List<StayRoom> viewAllRooms(){
		return stayRoomService.findAllStayRooms();
	}
	
	// C 房間
	@PostMapping
	@ResponseBody
	public StayRoom createStayRoom(@RequestBody StayRoom stayRoom) {
		return stayRoomService.saveStayRoom(stayRoom);
	}
	
	// U 房間
	@PutMapping("/{id}")
	@ResponseBody
	public StayRoom updataStayRoom(@PathVariable Integer id,@RequestBody StayRoom stayRoom) {
		
		stayRoom.setRoomId(id);
		
		return stayRoomService.saveStayRoom(stayRoom);
	}
	// D 房間 (軟刪除)
	@DeleteMapping("/{id}")
	@ResponseBody
	public boolean  deleteStayRoom(@PathVariable Integer id) {
		return stayRoomService.deleteStayRoom(id);
	}
}
