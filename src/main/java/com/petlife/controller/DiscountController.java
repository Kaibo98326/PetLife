package com.petlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/discount") // 統一路徑字首為 discount
public class DiscountController {

    @GetMapping("/index")
    public String discountIndex() {
        // 對應到 templates/discount.html
        return "discount"; 
    }
    
    // 未來你可以在這裡繼續增加 discountAdd 和 discountSearch 的路由...
}