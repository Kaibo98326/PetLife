package com.petlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petlife.model.Cart;


public interface ICartDao extends JpaRepository<Cart, Integer> {
    // 透過會員id找到該會員的購物車
    Cart findByMemberId(Integer memberId);
   
}
