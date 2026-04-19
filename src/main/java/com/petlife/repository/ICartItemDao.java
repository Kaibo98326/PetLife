package com.petlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.petlife.model.CartItem;

import java.util.List;

@Transactional
public interface ICartItemDao extends JpaRepository<CartItem, Integer> {
    // 根據購物車 ID 查詢所有項目
    List<CartItem> findByCartId(Integer cartId);
    
    // 結帳清空購物車
    void deleteByCartId(Integer cartId); 
   
}
