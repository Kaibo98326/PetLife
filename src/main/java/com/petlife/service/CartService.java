package com.petlife.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petlife.model.Cart;
import com.petlife.model.CartItem;
import com.petlife.model.Product;
import com.petlife.repository.ICartDao;
import com.petlife.repository.ICartItemDao;
import com.petlife.repository.IProductDao;

@Service
@Transactional
public class CartService {
	@Autowired
    private ICartDao cartDao;
	
    @Autowired
    private ICartItemDao cartItemDao;

    @Autowired
    private IProductDao productDao;
    
    // 取得或創建購物車
    public Cart getOrCreateCart(Integer memberId) {
        Cart cart = cartDao.findByMemberId(memberId);
        if (cart == null) {
            cart = new Cart();
            cart.setMemberId(memberId);
            cart.setCreatedAt(LocalDateTime.now());
            cart.setUpdatedAt(LocalDateTime.now());
            cart = cartDao.save(cart);
        }
        return cart;
    }

    // 加入商品到購物車
    public void addToCart(Integer memberId, CartItem newItem) {
        Cart cart = getOrCreateCart(memberId);
        Integer cartId = cart.getCartId();

        // --- 關鍵修正：從資料庫抓取真實的商品資訊 ---
        Product realProduct = productDao.findById(newItem.getProductId())
                .orElseThrow(() -> new RuntimeException("找不到該商品"));
        
        // 用真實的價格與名稱覆蓋掉前端傳來的不明數值
        BigDecimal realPrice = realProduct.getProductPrice(); 
        newItem.setProductPrice(realPrice);
        newItem.setProductName(realProduct.getProductName());
        // ---------------------------------------

        List<CartItem> items = cartItemDao.findByCartId(cartId);
        Optional<CartItem> existingItem = items.stream()
                .filter(i -> i.getProductId().equals(newItem.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + newItem.getQuantity());
            
            // 使用剛抓到的 realPrice 計算
            item.setSubtotal(realPrice.multiply(new BigDecimal(item.getQuantity())));
            cartItemDao.save(item);
        } else {
            newItem.setCartId(cartId);
            // 使用真實價格計算小計
            newItem.setSubtotal(realPrice.multiply(new BigDecimal(newItem.getQuantity())));
            cartItemDao.save(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartDao.save(cart);
    }

    // 取得購物車清單
    public List<CartItem> getCartItems(Integer memberId) {
        Cart cart = cartDao.findByMemberId(memberId);
        if (cart == null) return new ArrayList<>();
        return cartItemDao.findByCartId(cart.getCartId());
    }

    // 結帳清空購物車(軟刪除)
    public void clearCart(Integer cartId) {
        cartItemDao.deleteByCartId(cartId);
    }
    // 真的刪除
    public void removeItemFromCart(Integer itemId) {
        cartItemDao.deleteById(itemId);
    }
    
    // 取得購物車總商品件數 (加總所有 quantity)
    public Integer getCartTotalQuantity(Integer memberId) {
        Cart cart = cartDao.findByMemberId(memberId);
        if (cart == null) return 0;
        
        List<CartItem> items = cartItemDao.findByCartId(cart.getCartId());
        
        return items.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();
    }

}