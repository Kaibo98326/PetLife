package com.petlife.controller;



import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.petlife.model.Member;
import com.petlife.repository.MemberRepository;
import com.petlife.service.IMemberService;
import com.petlife.service.PasswordUtils;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberFontController {
	
	@Autowired
	private IMemberService memberService;
	@Autowired
	private MemberRepository memberRepo;
	
	//註冊會員
	@PostMapping("/register")
	public String register(@ModelAttribute Member member, Model m) {
	    String status = memberService.register(member);
	    m.addAttribute("status", status);
	    return "result1";
	}


	
	//登入會員
	@PostMapping("/login")
	public String login(@RequestParam String email,
	                    @RequestParam String password,
	                    Model m,
	                    HttpSession session) {
	    String status = memberService.login(email, password);

	    if ("login_success".equals(status)) {
	        Member member = memberService.findByEmail(email);
	        session.setAttribute("memberName", member.getMemberName());
	        session.setAttribute("memberId", member.getMemberId());
	    }

	    m.addAttribute("status", status);
	    return "result1";
	}




	
	//更新會員資料
	@PostMapping("/memberupdate")
	public String updateMember(@ModelAttribute Member member, Model m) {
	    String status = memberService.updateMember(member.getMemberId(), member);
	    m.addAttribute("status", status);
	    return "result1";
	}

	
	//會員登出
	@GetMapping("/logout")
	public String logout(HttpSession session, Model model) {
	    // 清除 session
	    session.invalidate();

	    // 提示狀態
	    model.addAttribute("status", "logout");

	    // 導向 result1.html (SweetAlert2 會顯示已登出)
	    return "result1";
	}
	
	//跳轉會員中心
	@GetMapping("/center")
	public String memberCenter(HttpSession session , Model m) {
		Object memberId = session.getAttribute("memberId");
		Object memberName = session.getAttribute("memberName");
		
		if(memberId == null) {
			//沒登入就導回登入頁
			return "redirect:/loginMember";
		}
		//把會員資訊放進Model
		m.addAttribute("memberId", memberId);
		m.addAttribute("memberName", memberName);
		//以後可擴充查詢紅利點數訂單紀錄之類的
		return "memberCenter" ;
	}

	@GetMapping("/edit")
    public String editMemberForm(Model model, @RequestParam("id") Integer memberId) {
        Member member = memberRepo.findById(memberId).orElseThrow();
        model.addAttribute("member", member);
        return "memberEdit"; // 對應上面的 Thymeleaf 頁面
    }

	@PostMapping("/update")
	public String updateMember(@ModelAttribute Member member,
            @RequestParam(required = false) String password,
            Model m) {
		Member dbMember = memberRepo.findById(member.getMemberId()).orElseThrow();

		boolean changed = false;
		
		// 檢查 Email 重複（排除自己）
	    Member existingByEmail = memberRepo.findByEmail(member.getEmail()).orElse(null);
	    if (existingByEmail != null && !existingByEmail.getMemberId().equals(member.getMemberId())) {
	        m.addAttribute("status", "duplicate_email");
	        m.addAttribute("memberId", member.getMemberId());
	        return "result1";
	    }

	    // 檢查 Phone 重複（排除自己）
	    boolean phoneExists = memberRepo.findAll().stream()
	        .anyMatch(mb -> mb.getPhone() != null &&
	                        mb.getPhone().equals(member.getPhone()) &&
	                        !mb.getMemberId().equals(member.getMemberId()));
	    if (phoneExists) {
	        m.addAttribute("status", "duplicate_phone");
	        m.addAttribute("memberId", member.getMemberId());
	        return "result1";
	    }
	
		if (!dbMember.getMemberName().equals(member.getMemberName())) {
			dbMember.setMemberName(member.getMemberName());
			changed = true;
		}
		if (!dbMember.getEmail().equals(member.getEmail())) {
			dbMember.setEmail(member.getEmail());
			changed = true;
		}
		if (!dbMember.getPhone().equals(member.getPhone())) {
			dbMember.setPhone(member.getPhone());
			changed = true;
		}
		if (!dbMember.getAddress().equals(member.getAddress())) {
			dbMember.setAddress(member.getAddress());
			changed = true;
		}
		if (password != null && !password.isEmpty()) {
			dbMember.setPasswordHash(PasswordUtils.hashPassword(password));
			changed = true;
		}

		if (!changed) {
			m.addAttribute("status", "nochange");
			return "result1"; // SweetAlert 顯示「沒有修改任何資料」
		}

		memberRepo.save(dbMember);
		m.addAttribute("status", "update_success");
		return "result1"; // SweetAlert 顯示「更新成功」}
	}


    @PostMapping("/checkPassword")
    @ResponseBody
    public Map<String, Boolean> checkPassword(@RequestBody Map<String, String> payload) {
        Integer memberId = Integer.valueOf(payload.get("memberId"));
        String oldPassword = payload.get("oldPassword");

        Member member = memberRepo.findById(memberId).orElseThrow();
        boolean valid = PasswordUtils.checkPassword(oldPassword, member.getPasswordHash());

        return Map.of("valid", valid);
    }




	
	
	
	
	
}
