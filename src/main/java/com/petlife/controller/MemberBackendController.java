package com.petlife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.petlife.model.Member;
import com.petlife.service.IMemberService;

@Controller
@RequestMapping("admin/members")
public class MemberBackendController {
	
	 	@Autowired
	    private IMemberService memberService;

	    // 查詢所有會員
	    @GetMapping
	    public String  findAll(Model m) {
	    		m.addAttribute("members", memberService.findAll());
	        return "admin/memberList";
	    }

	    // 查詢單一會員
	    @GetMapping("/{id}")
	    public String findById(@PathVariable Integer id ,Model m) {
	       m.addAttribute("member" , memberService.findById(id));
	       return "admin/memberDetail";
	    }

	    // 軟刪除會員
	    @PostMapping("/delete/{id}")
	    public String softDelete(@PathVariable Integer id) {
	        memberService.softDelete(id);
	        return "redirect:/admin/members"; // 導向後台會員管理頁面
	    }

	    // 停權會員
	    @PostMapping("/disable/{id}")
	    public String disable(@PathVariable("id") Integer memberId) {
	        memberService.disable(memberId);
	        return "redirect:/admin/members"; // 導向後台會員管理頁面
	    }

	    // 後台修改會員資料
	    @PostMapping("/update")
	    public String update(@ModelAttribute Member member) {
	        memberService.update(member);
	        return "redirect:/admin/members"; // 修改完成導向後台會員管理頁面
	    }

}
