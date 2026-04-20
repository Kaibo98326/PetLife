package com.petlife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petlife.model.StayRoomType;
import com.petlife.repository.StayRoomTypeRepository;

@Service
public class StayRoomTypeService {

	// DI
	@Autowired
	private StayRoomTypeRepository stayRoomTypeRepository;
	
	// C & U 房型
	public StayRoomType saveStayRoomType(StayRoomType stayRoomType) {
		return stayRoomTypeRepository.save(stayRoomType);
	}
	
	// 單筆ID查詢 房型
	public StayRoomType findStayRoomType(Integer stayRoomTypeid) {
		return stayRoomTypeRepository.findById(stayRoomTypeid).orElse(null);
	}
	
	// 全部查詢 房型
	public List<StayRoomType> findAllStayRoomTypes() {
		return stayRoomTypeRepository.findAll();
	}
	
	// 用ID刪除 房型
	public void DeleteStayRoomType(Integer stayRoomTypeid) {
		stayRoomTypeRepository.deleteById(stayRoomTypeid);
	}
}
