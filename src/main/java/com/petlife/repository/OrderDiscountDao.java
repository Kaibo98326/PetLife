package com.petlife.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.petlife.model.OrderDiscount;


@Repository
public class OrderDiscountDao {
    @Autowired
    private IOrderDiscountDao orderDiscountRepository;

    public boolean insertOrderDiscount(OrderDiscount bean) {
        return orderDiscountRepository.save(bean) != null;
    }

    public List<OrderDiscount> findDiscountsByOrderId(Integer orderId) {
        return orderDiscountRepository.findByOrderId(orderId);
    }
}