package com.petlife.service;

import org.springframework.stereotype.Service;

/**
 * 【臨時模擬用】
 * 等小吉 將此檔案刪除或覆蓋。
 */
@Service
public class CartService {

    /**
     * 模擬計算購物車總件數
     * 暫時回傳 0，確保前台 ProductDetail 頁面能正常顯示
     */
    public int getTotalQuantityByMemberId(Integer memberId) {
        // 先回傳 0 或是隨便一個數字（例如 5），讓你在 Demo 時畫面好看一點
        return 0; 
    }
}