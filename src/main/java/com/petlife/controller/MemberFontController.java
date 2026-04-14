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
	public String  register(@ModelAttribute Member member , Model m) {
		Member registered = memberService.register(member);
		if(registered != null ) {
			m.addAttribute("status" , "register_success");
		}else {
			m.addAttribute("status" , "register_fail");
		}
		return "result1";
	}
	
	//登入會員
	@PostMapping("/login")
	public String login(@RequestParam String email ,
						@RequestParam String password ,
						Model m , HttpSession session) {
		Member member = memberService.login(email, password);
		if(member != null) {
			m.addAttribute("status" , "login_success");
			session.setAttribute("memberName", member.getMemberName());
			session.setAttribute("memberId", member.getMemberId());
		}else {
			m.addAttribute("status", "wrongpassword");
		}
		return "result1";
	}
	
	//更新會員資料
	@PutMapping("/update")
	public String  update(@ModelAttribute Member member , Model m) {
		memberService.update(member);
		m.addAttribute("status" , "update_success");
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
    public String updateMember(@ModelAttribute Member member ,@RequestParam(required = false ) String password) {
    		Member dbMember = memberRepo.findById(member.getMemberId()).orElseThrow();
    		//更新允許修改的欄位
    		dbMember.setMemberName(member.getMemberName());
    		dbMember.setEmail(member.getEmail());
    	    dbMember.setPhone(member.getPhone());
    	    dbMember.setAddress(member.getAddress());
        // 如果有密碼欄位，記得加密處理
        if (password != null && !password.isEmpty()) {
            member.setPasswordHash(PasswordUtils.hashPassword(password));
        }
        memberRepo.save(member);
        return "redirect:/member/center"; // 修改完成後回會員中心
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
