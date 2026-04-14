package com.petlife.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.petlife.model.Order;

@Repository
public class OrderDao {

	@Autowired
	private IOrderDao orderRepository;

	// 結帳：產生新訂單並回傳ID
	public Integer insertOrder(Order order) {
		Order savedOrder = orderRepository.save(order);
		return savedOrder.getOrderId();
	}

	public Order queryOrderById(Integer orderId) {
		return orderRepository.findById(orderId).orElse(null);
	}

	public boolean updateOrderStatus(Integer orderId, String status) {
		return orderRepository.findById(orderId).map(order -> {
			order.setOrderStatus(status);
			orderRepository.save(order);
			return true;
		}).orElse(false);
	}

	public List<Order> queryHistoryAllOrder(Integer memberId) {
		return orderRepository.findByMemberIdOrderByOrderDateDesc(memberId);
	}

	public List<Order> queryAllOrdersForAdmin() {
		return orderRepository.findAllByOrderByOrderDateAsc();
	}

	public void deleteOrder(Integer orderId) {
		orderRepository.deleteById(orderId);
	}
	
	// OrderDao.java

	public BigDecimal calculateCartTotal(Integer cartId) {
	    return orderRepository.getCartTotal(cartId); 
	}
	
	
}