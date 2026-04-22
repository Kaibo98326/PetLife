package com.petlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/discount")
public class DiscountController {

    // 當前端 AJAX 呼叫 /discount/index 時，就把整包 discount.html 送過去
    @GetMapping("/index")
    public String showDiscountPage() {
        return "discount"; // 對應到 src/main/resources/templates/discount.html
    }
}