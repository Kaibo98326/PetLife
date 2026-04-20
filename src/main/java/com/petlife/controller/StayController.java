package com.petlife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.petlife.model.Pet;
import com.petlife.model.Stay;
import com.petlife.repository.PetRepository;
import com.petlife.service.PetService;
import com.petlife.service.StayService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/hotel/orders")
public class StayController {

	// DI
	@Autowired
	private StayService stayService;

	// 網址
	@GetMapping
	public String page() {
		return "hotelOrders";
	}

	// R 全部
	@GetMapping("/data")
	@ResponseBody
	public List<Stay> viewAllStays() {
		return stayService.findAllStays();
	}

	// R 單筆資料
	@GetMapping("/{id:\\d+}")
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
	@PutMapping("/{id:\\d+}")
	@ResponseBody
	public Stay updataStay(@PathVariable Integer id, @RequestBody Stay stay) {
		stay.setStayId(id);
		return stayService.saveStay(stay);
	}

	// D soft delete
	@DeleteMapping("/{id:\\d+}")
	@ResponseBody
	public void deleteStay(@PathVariable Integer id) {
		stayService.deleteStay(id);
	}

	// 電話末三碼查詢
	@GetMapping("/search")
	@ResponseBody
	public List<Stay> searchByPhone(@RequestParam String phone) {
		return stayService.findByPhone(phone);
	}

	// 訂房頁面（會員端）
	@GetMapping("/booking")
	public String bookingPage(HttpSession session, Model model) {
		Object memberId = session.getAttribute("memberId");
		if (memberId == null) {
			// 沒登入 → 導到 SweetAlert 提示頁
			model.addAttribute("status", "need_login");
			return "result1";
		}
		model.addAttribute("memberId", memberId);
		model.addAttribute("memberName", session.getAttribute("memberName"));
		return "stayBooking"; // 訂房頁面
	}

	// API 讓前端session抓寵物清單
	@Autowired
	private PetService petService;

	@GetMapping("/booking/pets")
	@ResponseBody
	public List<Pet> getMemberPets(HttpSession session) {
	    Integer memberId = (Integer) session.getAttribute("memberId");
	    return petService.findActivePetsByMemberId(memberId); 
	   	}

	// 確認訂單頁面
	@GetMapping("/confirm")
	public String confirmPage(HttpSession session, Model model) {
		Object memberId = session.getAttribute("memberId");
		if (memberId == null) {
			model.addAttribute("status", "need_login");
			return "result1";
		}
		return "stayConfirm";
	}

	// 歷史訂單頁面
	@GetMapping("/history")
	public String historyPage(HttpSession session, Model model) {
		Object memberId = session.getAttribute("memberId");
		if (memberId == null) {
			model.addAttribute("status", "need_login");
			return "result1";
		}
		return "stayHistory";
	}

	// 歷史訂單 API（只回傳此會員的訂單）
	@GetMapping("/history/data")
	@ResponseBody
	public List<Stay> getMyOrders(HttpSession session) {
		Integer memberId = (Integer) session.getAttribute("memberId");
		return stayService.findByMemberId(memberId);
	}
}
