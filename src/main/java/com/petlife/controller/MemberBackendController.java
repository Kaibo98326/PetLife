package com.petlife.controller;



import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.petlife.model.Member;
import com.petlife.service.IMemService;


@Controller
@RequestMapping("/members")
public class MemberBackendController {
	
	 	@Autowired
	    private IMemService memberService;

	 	 // 一般清單分頁
	    @GetMapping("/list")
	    public String listMembers(@RequestParam(defaultValue = "0") int page,
	                              Model model) {
	        Page<Member> memberPage = memberService.findAll(PageRequest.of(page, 10));
	        model.addAttribute("members", memberPage.getContent());
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", memberPage.getTotalPages());
	        return "memberlist :: listFragment";
	    }

	    // 搜尋分頁
	    @GetMapping("/search")
	    public String searchMembers(@RequestParam String memberName,
	                                @RequestParam(defaultValue = "0") int page,
	                                Model model) {
	        Page<Member> memberPage = memberService.searchByName(memberName, PageRequest.of(page, 10));
	        model.addAttribute("members", memberPage.getContent());
	        model.addAttribute("currentPage", page);
	        model.addAttribute("totalPages", memberPage.getTotalPages());
	        model.addAttribute("searchKeyword", memberName);
	        return "memberlist :: listFragment";
	    }
	    
	 // 更新會員資料
	    @PostMapping("/update/{id}")
	    @ResponseBody
	    public ResponseEntity<?> updateMember(@PathVariable Integer id, @RequestBody Member updatedMember) {
	        Member member = memberService.updateMember(id, updatedMember);
	        if (member == null) {
	            return ResponseEntity.notFound().build();
	        }
	        return ResponseEntity.ok().build();
	    }

	    @PostMapping("/changePassword/{id}")
	    @ResponseBody
	    public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
	        String newPassword = payload.get("password");
	        Member member = memberService.changePassword(id, newPassword);
	        if (member == null) {
	            return ResponseEntity.notFound().build();
	        }
	        return ResponseEntity.ok().build();
	    }






}
