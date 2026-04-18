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
	    public String searchMembers(@RequestParam(value="memberName", required=false) String name,
	                                @RequestParam(defaultValue = "0") int page,
	                                Model model) {
	        // 如果沒有輸入關鍵字 → 回到一般清單
	        if (name == null || name.isBlank()) {
	            return listMembers(page, model); // 呼叫一般清單方法
	        }

	        // page 保護，避免負數
	        int safePage = page < 0 ? 0 : page;
	        int pageSize = 10;

	        // 查詢
	        Page<Member> memberPage = memberService.searchByName(name, PageRequest.of(safePage, pageSize));

	        // 放入 model
	        model.addAttribute("members", memberPage.getContent());
	        model.addAttribute("currentPage", safePage);
	        model.addAttribute("totalPages", memberPage.getTotalPages());
	        model.addAttribute("searchKeyword", name);

	        System.out.println(">>> 搜尋關鍵字: " + name + ", page=" + page);

	        // 回傳 fragment
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
