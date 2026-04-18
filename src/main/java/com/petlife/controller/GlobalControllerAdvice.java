package com.petlife.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.petlife.service.ProductService;

/**
 * 💡 修改說明：
 * 1. @ControllerAdvice 是 Spring 的「全域切面」，它會攔截所有的 Controller。
 * 2. @ModelAttribute 方法會在每一個請求執行前先跑一次。
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ProductService productService;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        // 💡 關鍵：無論使用者在哪個功能頁面，都自動計算庫存警示數並塞進 Model
        long count = productService.getLowStockCount();
        
        // 這個名稱必須跟你在 HTML 裡寫的 th:if="${lowStockCount}" 一致
        model.addAttribute("lowStockCount", count);
        
        System.out.println("--- 全域庫存追蹤：目前有 " + count + " 筆警告 ---");
    }
}