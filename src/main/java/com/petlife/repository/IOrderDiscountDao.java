package com.petlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petlife.model.OrderDiscount;

public interface IOrderDiscountDao extends JpaRepository<OrderDiscount, Integer> {
    List<OrderDiscount> findByOrderId(Integer orderId);
}

