package com.petlife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.petlife.model.Order;
import com.petlife.model.OrderDetail;
import com.petlife.repository.IOrderDao;
import com.petlife.repository.IOrderDetailDao;

@Controller
@RequestMapping("/api/order")
public class OrderAdminController {

    @Autowired
    private IOrderDao iod;
    @Autowired
    private IOrderDetailDao iodd;

 // 搜尋
    @GetMapping("/all")
    public String getAllOrders(@RequestParam(required = false) String search, Model model) {
        List<Order> list;
        if (search != null && !search.trim().isEmpty()) {
            // 執行模糊查詢
            list = iod.findByOrderNameContainingOrderByOrderDateAsc(search);
            model.addAttribute("searchQuery", search);
        } else {
            list = iod.findAllByOrderByOrderDateAsc();
        }
        model.addAttribute("orders", list);
        return "orderAdmin"; 
    }
    
    @GetMapping("/detail/{orderId}")
    public String getOrderDetail(@PathVariable Integer orderId, Model model) {
        Order order = iod.findById(orderId).orElse(null);
        if (order == null) return "error"; 

        List<OrderDetail> details = iodd.findByOrderBean_OrderId(orderId);
        model.addAttribute("order", order);
        model.addAttribute("details", details);
        
        return "orderDetailAdmin"; 
    }

    @PostMapping("/updateOrder/{orderId}")
    @ResponseBody 
    public ResponseEntity<?> updateOrder(@PathVariable Integer orderId, @RequestBody java.util.Map<String, String> payload) {
        String status = payload.get("orderStatus");
        String payment = payload.get("orderPayment");

        return iod.findById(orderId).map(order -> {
            order.setOrderStatus(status);
            order.setOrderPayment(payment);
            iod.save(order);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deleteOrder/{orderId}")
    @ResponseBody
    public ResponseEntity<?> deleteOrder(@PathVariable Integer orderId) {
        if (iod.existsById(orderId)) {
            iod.deleteById(orderId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}