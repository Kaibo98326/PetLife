package com.petlife.controller;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.petlife.model.Order;
import com.petlife.model.OrderDetail;
import com.petlife.service.OrderService; 

@Controller
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public String createOrder(
            @RequestParam String orderName,
            @RequestParam String orderPhone,
            @RequestParam String orderAddress,
            @RequestParam String orderPayment,
            @RequestParam(required = false) String orderNote,
            @RequestParam Integer cartId,
            HttpSession session) {

        Integer memberId = (Integer) session.getAttribute("memberId");
        if (memberId == null) return "redirect:/loginChoice";

        try {
            // 執行 Service：包含存訂單、扣庫存、存明細、清空購物車
            Integer newOrderId = orderService.createOrderFromCart(
                memberId, cartId, orderName, orderPhone, orderAddress, orderPayment, orderNote
            );

            // 成功後重導向，避免按 F5 導致重複下單
            return "redirect:/api/order/success?orderId=" + newOrderId;

        } catch (RuntimeException e) {
            // 捕捉剛才發生的「購物車空了」或其他異常，導向錯誤提示頁面或回購物車
            System.err.println("下單失敗：" + e.getMessage());
            return "redirect:/cart/view?msg=" + e.getMessage(); 
        }
    }

    @GetMapping("/success")
    public String showSuccessPage(@RequestParam Integer orderId, Model model) {
        System.out.println("DEBUG: 進入成功頁面，訂單ID = " + orderId);
        
        Order order = orderService.getOrderMain(orderId);
        List<OrderDetail> items = orderService.getOrderItems(orderId);
        
        System.out.println("DEBUG: 撈出的明細筆數 = " + items.size());

        model.addAttribute("orderMain", order);
        model.addAttribute("orderItems", items);
        
        return "OrderSuccess"; 
    }
}