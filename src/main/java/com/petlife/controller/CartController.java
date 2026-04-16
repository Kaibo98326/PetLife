package com.petlife.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.petlife.model.CartItem;
import com.petlife.service.CartService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/cart")
public class CartController {
	@Autowired
    private CartService cartService;

    // 查看購物車
    @GetMapping("/{memberId}")
    public ResponseEntity<List<CartItem>> getMyCart(@PathVariable Integer memberId) {
        return ResponseEntity.ok(cartService.getCartItems(memberId));
    }
    
    // 取得購物車商品總件數
    @GetMapping("/count/{memberId}")
    public ResponseEntity<Integer> getCartCount(@PathVariable Integer memberId) {
        Integer count = cartService.getCartTotalQuantity(memberId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        Integer memberId = (Integer) session.getAttribute("memberId");
        if (memberId == null) {
            return "redirect:/loginChoice";
        }

        List<CartItem> cartItems = cartService.queryCartItemsByMemberId(memberId);
        
        // 計算總計金額
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 計算總件數
        int totalQty = cartItems.stream().mapToInt(CartItem::getQuantity).sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("cartTotalQty", totalQty); 
        
        return "cart"; 
    }
    
 // 加入購物車 API
    @PostMapping("/add/{memberId}")
    @ResponseBody // 沒有這個AJAX就會跳404或500
    public ResponseEntity<String> addItem(@PathVariable Integer memberId, @RequestBody CartItem item) {
        try {
            cartService.addToCart(memberId, item);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    // 刪除特定品項
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<String> removeItem(@PathVariable Integer itemId) {
        cartService.removeItemFromCart(itemId);
        return ResponseEntity.ok("已從購物車移除");
    }
    
    //處理購物車頁面的加減與刪除
    @PostMapping("/update")
    public String updateItem(@RequestParam Integer itemId, 
                             @RequestParam Integer quantity, 
                             @RequestParam String action) {
        if ("delete".equals(action)) {
            cartService.removeItemFromCart(itemId);
        } else {
        	// 補上這一行，讓數量的變動存進資料庫
            cartService.updateCartItemQuantity(itemId, quantity);
        }
        return "redirect:/api/cart/cart"; // 執行完動作，重導向回購物車頁面
    }
}