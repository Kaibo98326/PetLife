package com.petlife.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petlife.model.Discount;
import com.petlife.repository.DiscountRepository;

import java.util.List;
//後端邏輯層
@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    // 取得所有折扣活動
    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    // 儲存或更新活動 (這裡可以加入你之後需要的防呆驗證邏輯)
    public Discount saveDiscount(Discount discount) {
        // 如果是百分比折扣，確保數值轉換 (前端傳 85 -> 後端存 0.85) 的邏輯也可以寫在這裡
        return discountRepository.save(discount);
    }
}