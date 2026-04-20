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
	 	

	 	// 會員清單
	 	@GetMapping("/list")
	 	public String memberList(@RequestParam(defaultValue = "0") int page, Model m) {
	 	    int pageSize = 10;
	 	    Page<Member> memberPage = memberService.findAll(PageRequest.of(page, pageSize));

	 	    m.addAttribute("members", memberPage.getContent());
	 	    m.addAttribute("currentPage", page);
	 	    m.addAttribute("totalPages", memberPage.getTotalPages());

	 	    return "memberlist :: listFragment";
	 	}

	 // 模糊查詢姓名
	 	@GetMapping("/search")
	 	public String searchMemberByName(@RequestParam(value="memberName", required=false) String name,
	 	                                 @RequestParam(defaultValue = "0") int page,
	 	                                 Model m) {
	 	    // 空字串或 null → 回到一般清單
	 	    if (name == null || name.isBlank()) {
	 	        return memberList(page, m);
	 	    }

	 	    // page 保護，避免負數
	 	    int safePage = page < 0 ? 0 : page;
	 	    int pageSize = 10;

	 	    Page<Member> memberPage = memberService.searchByName(name, PageRequest.of(safePage, pageSize));

	 	    m.addAttribute("members", memberPage.getContent());
	 	    m.addAttribute("currentPage", safePage);
	 	    m.addAttribute("totalPages", memberPage.getTotalPages());
	 	    m.addAttribute("searchKeyword", name);

	 	    System.out.println(">>> 搜尋關鍵字: " + name + ", page=" + page);

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
	    
	    // 顯示新增會員表單 (AJAX 載入 fragment)
	    @GetMapping("/addForm")
	    public String showAddForm(Model m) {
	        m.addAttribute("member", new Member());
	        return "addAdminMemberForm :: addFormFragment";
	    }

	    // 後台新增會員
	    @PostMapping("/add")
	    @ResponseBody
	    public String addMember(@RequestBody Member member) {
	        String result = memberService.register(member);
	        if ("register_success".equals(result)) {
	            return "success";
	        } else {
	            return "duplicate"; // email 或 phone 已存在
	        }
	    }
	    
	    







}
