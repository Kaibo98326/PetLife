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
import com.petlife.repository.ProductRepository;

@Service
@Transactional
public class CartService {
	@Autowired
	private ICartDao cartDao;

	@Autowired
	private ICartItemDao cartItemDao;

	@Autowired
	private ProductRepository pr;

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
		Product realProduct = pr.findById(newItem.getProductId()).orElseThrow(() -> new RuntimeException("找不到該商品"));

		// 用真實的價格與名稱覆蓋掉前端傳來的不明數值
		BigDecimal realPrice = realProduct.getProductPrice();
		newItem.setProductPrice(realPrice);
		newItem.setProductName(realProduct.getProductName());
		// ---------------------------------------
		// --- 防呆：如果折扣是空的，一定要設為 0，否則資料庫公式會算不出小計 ---
		if (newItem.getDiscountAmount() == null) {
			newItem.setDiscountAmount(BigDecimal.ZERO);
		}

		List<CartItem> items = cartItemDao.findByCartId(cartId);
		Optional<CartItem> existingItem = items.stream().filter(i -> i.getProductId().equals(newItem.getProductId()))
				.findFirst();

		if (existingItem.isPresent()) {
			CartItem item = existingItem.get();
			item.setQuantity(item.getQuantity() + newItem.getQuantity());
			// 這裡絕對不要寫 setSubtotal
			cartItemDao.save(item);
		} else {
			newItem.setCartId(cartId);
			// 這裡絕對不要寫 setSubtotal
			cartItemDao.save(newItem);
		}
	}
	
	// +跟-按鈕
	public void updateCartItemQuantity(Integer itemId, Integer quantity) {
	    CartItem item = cartItemDao.findById(itemId)
	            .orElseThrow(() -> new RuntimeException("找不到該購物車品項"));
	    
	    // 設定新數量
	    item.setQuantity(quantity);
	    
	    // 防呆：確保折扣不是 NULL，否則小計會變NULL
	    if (item.getDiscountAmount() == null) {
	        item.setDiscountAmount(BigDecimal.ZERO);
	    }
	    // 存檔，SQLServer會自動更新subtotal
	    cartItemDao.save(item);
	}

	// 取得購物車清單
	public List<CartItem> getCartItems(Integer memberId) {
		Cart cart = cartDao.findByMemberId(memberId);
		if (cart == null)
			return new ArrayList<>();
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
		if (cart == null)
			return 0;

		List<CartItem> items = cartItemDao.findByCartId(cart.getCartId());

		return items.stream().mapToInt(CartItem::getQuantity).sum();
	}

	// 統計右上角購物車圖案顯示現有購物車商品數量使用的查詢方法
	public List<CartItem> queryCartItemsByMemberId(Integer memberId) {
		Cart cart = cartDao.findByMemberId(memberId);
		if (cart == null)
			return new ArrayList<>();

		List<CartItem> items = cartItemDao.findByCartId(cart.getCartId());

		for (CartItem item : items) {
			// 如果資料庫抓出來的小計是 NULL，現場幫它補算，避免前端噴錯
			if (item.getSubtotal() == null && item.getProductPrice() != null) {
				item.setSubtotal(item.getProductPrice().multiply(new BigDecimal(item.getQuantity())));
			}
		}
		return items;
	}
	// 計算購物車總金額
	public BigDecimal calculateCartTotal(Integer memberId) {
	    List<CartItem> items = queryCartItemsByMemberId(memberId);
	    return items.stream()
	            .map(item -> {
	                // 如果資料庫沒算好小計，這裡手動補強
	                if (item.getSubtotal() == null) {
	                    return item.getProductPrice().multiply(new BigDecimal(item.getQuantity()));
	                }
	                return item.getSubtotal();
	            })
	            .reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}