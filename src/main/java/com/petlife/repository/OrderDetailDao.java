package com.petlife.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.petlife.model.Order;
import com.petlife.model.OrderDetail;

@Repository
public class OrderDetailDao {
	@Autowired
	private IOrderDetailDao orderDetailRepository;

	public void saveAll(List<OrderDetail> details) {
		orderDetailRepository.saveAll(details);
	}

	public List<OrderDetail> queryItemsByOrderId(Order orderId) {
		return orderDetailRepository.findByOrderBean(orderId);
	}
	public boolean insertItemsFromCart(int orderId, Integer cartId) {
	    try {
	        // 呼叫 Repository 執行資料庫搬移動作
	        orderDetailRepository.transferCartToOrderDetails(orderId, cartId);
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
}