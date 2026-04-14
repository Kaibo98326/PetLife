package com.petlife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petlife.model.CartItem;
import com.petlife.service.CartService;

@RestController
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
    
    // 加入購物車
    @PostMapping("/add/{memberId}")
    public ResponseEntity<String> addItem(@PathVariable Integer memberId, @RequestBody CartItem item) {
        try {
            cartService.addToCart(memberId, item);
            return ResponseEntity.ok("成功加入購物車");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("加入失敗: " + e.getMessage());
        }
    }
    
    // 刪除特定品項
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<String> removeItem(@PathVariable Integer itemId) {
        cartService.removeItemFromCart(itemId);
        return ResponseEntity.ok("已從購物車移除");
    }
}