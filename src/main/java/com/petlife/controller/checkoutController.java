package com.petlife.controller;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.petlife.service.CartService;
import com.petlife.service.MemberService;
import com.petlife.model.Member; // 假設你有這個 Model

@Controller
@RequestMapping("/api/checkout") // 設定結帳流程的基礎路徑
public class checkoutController {

    @Autowired
    private CartService cartService;
    @Autowired
    private MemberService memberService;

 // 前往結帳頁面 (只保留這一個版本)
    @GetMapping("/page")
    public String showCheckoutPage(@RequestParam Integer cartId, Model model, HttpSession session) {
        // 1. 登入檢查
        Integer memberId = (Integer) session.getAttribute("memberId");
        if (memberId == null) {
            return "redirect:/loginChoice"; 
        }

        // 2. 取得資料
        BigDecimal total = cartService.calculateCartTotal(memberId);
        Member member = memberService.findById(memberId);

        // 3. 準備資料給 Checkout.html
        model.addAttribute("member", member); 
        model.addAttribute("totalAmount", total);
        model.addAttribute("cartId", cartId);
        
        // 4. 回傳頁面 (請確保 templates 下有 Checkout.html 且裡面沒寫 orderMain)
        return "Checkout"; 
    }

}