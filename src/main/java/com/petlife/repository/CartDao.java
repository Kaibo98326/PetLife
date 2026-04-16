package com.petlife.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.petlife.model.Cart;

@Repository
public class CartDao {

    @Autowired
    private ICartDao cartRepository;

    // 取得會員的購物車
    public Cart getCartByMemberId(Integer memberId) {
        return cartRepository.findByMemberId(memberId);
    }

    // 建立新購物車
    public Cart createCart(Cart cart) {
        return cartRepository.save(cart);
    }
    
    public void clearCartByCartId(Integer cartId) {
        // 呼叫 cartRepository 的刪除方法
        cartRepository.deleteById(cartId);
    }
}