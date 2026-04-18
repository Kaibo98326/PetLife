package com.petlife.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petlife.model.Stay;
import com.petlife.model.StayRoom;
import com.petlife.repository.StayRepository;
import com.petlife.repository.StayRoomRepository;

@Service
public class StayService {

	// DI
	@Autowired
	private StayRepository stayRepository;
	
	// 等等會用到
	@Autowired
	private StayRoomRepository stayRoomRepository;
	
	//  U
	public Stay saveStay(Stay stay) {
		return stayRepository.save(stay);
	}
	
	// R單筆 用ID
	public Stay findStay(Integer stayId) {
		return stayRepository.findById(stayId).orElse(null);
	}
	
	// R全部 用List
	public List<Stay> findAllStays() {
		return stayRepository.findAll();
	}
	
	// soft delete 用ID
	public boolean deleteStay(Integer stayId) {
		return stayRepository.findById(stayId)
				.map(stay -> {
					stay.setStayStatus("已取消");
					stayRepository.save(stay);
					return true;
					}
				).orElse(false);
	}
	
	// 計算天數邏輯
	// LocalDate 有一個內建方法叫做 .until() 
	// 可以直接幫你算時間 這裡我們用ChronoUnit.DAYS
	public long calculateDays(LocalDate startDate, LocalDate endDate) {
	    return startDate.until(endDate,  ChronoUnit.DAYS );
	//  import java.time.temporal.ChronoUnit;
	}

	// 計價邏輯 : 建立訂單，改房間狀態
	public Stay createStay(Stay stay) {
		//.until() 給的值會是long 所以我們用(int)強制轉型 
	    long days = calculateDays(stay.getStayStartDate(), stay.getStayEndDate());
	    stay.setStayDay((int) days);  
	    stay.setSumPrice(days * stay.getOrderPrice());  
	    stay.setStayStatus("已預約");  
	    
	    StayRoom room = stay.getStayRoom();
		room.setRoomStatus("已預約");
	    stayRoomRepository.save(room);
	    
	    return stayRepository.save(stay);
	}
	
	//電話末三碼 查詢邏輯
	public List<Stay> findByPhone(String phone){
		return stayRepository.findByPet_Member_PhoneEndingWith(phone);
	}

}
