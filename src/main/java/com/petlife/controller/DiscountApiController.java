package com.petlife.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.petlife.model.Discount;
import com.petlife.service.DiscountService;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountApiController {

    @Autowired
    private DiscountService discountService;

    // 前端 Ajax 呼叫此 API 即可取得 JSON 格式的活動列表
    @GetMapping
    public ResponseEntity<List<Discount>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    // 接收前端送來的新增/修改請求
    @PostMapping
    public ResponseEntity<Discount> saveDiscount(@RequestBody Discount discount) {
        return ResponseEntity.ok(discountService.saveDiscount(discount));
    }
}