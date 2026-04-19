package com.petlife.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

import com.petlife.model.Discount;
// import com.petlife.repository.DiscountProductRepository; (未來加上關聯表時才需要)

@Service
public class DiscountCalculationService {

    // @Autowired
    // private DiscountProductRepository discountProductRepository;

    /**
     * 結帳時的主程式：計算這台購物車總共可以「折抵多少錢」
     */
    public BigDecimal calculateDiscountAmount(List<CartItem> cartItems, Discount discount) {

        // 步驟一：資格過濾 (Eligibility Checker)
        // 計算出這台購物車裡，"真正符合該活動資格" 的商品總金額
        BigDecimal eligibleTotal = calculateEligibleTotal(cartItems, discount.getDiscountId());

        // 步驟二：門檻判斷
        // 如果符合資格的商品總額，連最低消費門檻都沒達到，就不能折抵 (回傳 0 元)
        if (eligibleTotal.compareTo(discount.getMinimumPurchaseAmount()) < 0) {
            return BigDecimal.ZERO;
        }

        // 步驟三：折扣計算機 (Discount Calculator)
        // 既然達標了，就把「符合資格的總金額」丟給計算機去算數學
        return applyMath(eligibleTotal, discount);
    }


    // =========================================================
    // 獨立模組 1：負責「挑商品」(未來關聯表加進來，只要改這裡就好)
    // =========================================================
    private BigDecimal calculateEligibleTotal(List<CartItem> cartItems, Integer discountId) {
        
        // 【現在的寫法】：沒有商品關聯表，全館適用。直接把整台購物車加總！
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            total = total.add(item.getSubtotal()); // 假設有 getSubtotal() 取得該品項總價
        }
        return total;

        /* // 【未來的寫法】：當你加上 DiscountProduct 表後，只要把上面的代碼換成這段
        List<Integer> eligibleProductIds = discountProductRepository.findProductIdsByDiscountId(discountId);

        // 如果資料庫沒設定指定商品，視為「全館活動」
        if (eligibleProductIds.isEmpty()) {
            return cartItems.stream().map(CartItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // 如果有指定商品，就「只加總有在名單內的商品」
        return cartItems.stream()
                .filter(item -> eligibleProductIds.contains(item.getProductId()))
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        */
    }


    // =========================================================
    // 獨立模組 2：負責「算數學」(不管商品怎麼挑，數學公式永遠不變)
    // =========================================================
    private BigDecimal applyMath(BigDecimal eligibleTotal, Discount discount) {
        Integer typeId = discount.getDiscountType().getDiscountTypeId();

        if (typeId == 1) { 
            // 類型 1：百分比折扣 (打折)
            // 假設資料庫 discountValue 存 0.85，代表打 85 折
            // 客人可以「省下」的錢 = 總額 * (1 - 0.85) = 總額 * 0.15
            BigDecimal discountRate = discount.getDiscountValue(); 
            BigDecimal offMultiplier = BigDecimal.ONE.subtract(discountRate); 
            return eligibleTotal.multiply(offMultiplier);

        } else if (typeId == 2) {
            // 類型 2：滿額折現
            // 假設資料庫 discountValue 存 100，代表折 100 元
            BigDecimal discountAmount = discount.getDiscountValue();
            
            // 防呆：如果客人買了 80 元，但活動折 100 元，最多只能折 80 元 (不能倒貼現金)
            if (eligibleTotal.compareTo(discountAmount) < 0) {
                return eligibleTotal;
            }
            return discountAmount;
        }

        return BigDecimal.ZERO; // 未知類型不給折扣
    }
}