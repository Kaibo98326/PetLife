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

    // 前端 Ajax 呼叫此 API 即可取得 JSON 格式的活動列表 (原本的，不動)
    @GetMapping
    public ResponseEntity<List<Discount>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    // 接收前端送來的新增請求 (原本的，不動)
    @PostMapping
    public ResponseEntity<Discount> saveDiscount(@RequestBody Discount discount) {
        return ResponseEntity.ok(discountService.saveDiscount(discount));
    }

    

    // 新增接收「修改」的 PUT 請求
    // 網址會是 /api/discounts/{id}，例如 /api/discounts/1
    @PutMapping("/{id}")
    public ResponseEntity<Discount> updateDiscount(@PathVariable Integer id, @RequestBody Discount discount) {
        // 雙重保險：強制把網址上的 ID 塞進物件裡，避免 Hibernate 把它當成「新增」來處理
        discount.setDiscountId(id);
        
        // 呼叫 Service 儲存 (如果有相同 ID，JPA 會自動執行 UPDATE 語法)
        return ResponseEntity.ok(discountService.saveDiscount(discount));
    }

    // 新增接收「刪除」的 DELETE 請求
    // 網址會是 /api/discounts/{id}，例如 /api/discounts/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Integer id) {
        // 呼叫 Service 執行刪除
        // 注意：請確認你的 DiscountService 裡面有寫 deleteDiscount(Integer id) 或 deleteById(Integer id) 這個方法
        discountService.deleteDiscount(id); 
        
        // 刪除成功後，回傳 200 OK 狀態碼給前端，不需要回傳內容 (Void)
        return ResponseEntity.ok().build();
    }
}