package com.petlife.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.petlife.model.CartItem;

@Repository
public class CartItemDao {

    @Autowired
    private ICartItemDao cartItemRepository;

    // 新增或更新項目
    public CartItem save(CartItem item) {
        return cartItemRepository.save(item);
    }

    // 刪除單一項目
    public void delete(Integer cartItemId) {
    	cartItemRepository.deleteById(cartItemId);
    }

    // 清空特定購物車的所有項目
    public void deleteByCartId(Integer cartId) {
        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        cartItemRepository.deleteAll(items);
    }
}