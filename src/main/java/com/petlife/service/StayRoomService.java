package com.petlife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petlife.model.StayRoom;
import com.petlife.repository.StayRoomRepository;

@Service
public class StayRoomService {

	//DI
	@Autowired
	private StayRoomRepository stayRoomRepository;
	
	// C & U 房間
	public StayRoom saveStayRoom(StayRoom stayRoom) {
		return stayRoomRepository.save(stayRoom);
	}
	
	// R 單筆房間資料 用 ID
	public StayRoom findStayRoom(Integer stayRoomId) {
		return stayRoomRepository.findById(stayRoomId).orElse(null);
	}
	
	// R 全部房間資料 用 List
	public List<StayRoom> findAllStayRooms(){
		return stayRoomRepository.findAll();
	}
	
	// 軟刪除：把 room_status 改成「停用」
	// void 改成 boolean
	public boolean deleteStayRoom(Integer stayRoomId) {
	    // 步驟一：先用 findById 去資料庫找這間房間，
	    // 如果找到了，把操作放進 .map()，找不到就用 .orElse() 回傳 false
	    return stayRoomRepository.findById(stayRoomId)
	            .map(room -> {	                
	                // 步驟二：把狀態設為「停用」
	                room.setRoomStatus("停用");
	                // 步驟三：存回資料庫
	                stayRoomRepository.save(room);
	                return true;  // 成功
	            }).orElse(false); // 找不到這筆資料
	}
	
}
