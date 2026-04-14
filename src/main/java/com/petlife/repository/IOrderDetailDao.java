package com.petlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.petlife.model.Order;
import com.petlife.model.OrderDetail;
import com.petlife.model.OrderDetailId;

import jakarta.transaction.Transactional;

import java.util.List;

public interface IOrderDetailDao extends JpaRepository<OrderDetail, OrderDetailId> {
	// 參數是 Order 物件，對應實體類別裡的 orderBean 屬性
	List<OrderDetail> findByOrderBean(Order orderId);

	void deleteByOrderBean_OrderId(Integer orderId);

	// IOrderDetailDao.java (Repository 介面)

	@Modifying // 注意：涉及新增/修改/刪除的 @Query 必須加這個
	@Transactional
	@Query(value = "INSERT INTO order_detail (order_id, product_id, quantity, unit_price) "
			+ "SELECT :orderId, p.product_id, c.quantity, p.product_price " + "FROM cart_item c "
			+ "JOIN product p ON c.product_id = p.product_id " + "WHERE c.cart_id = :cartId", nativeQuery = true)
	void transferCartToOrderDetails(@Param("orderId") int orderId, @Param("cartId") Integer cartId);

}
