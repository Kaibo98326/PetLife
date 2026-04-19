package com.petlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.petlife.model.Order;

import java.math.BigDecimal;
import java.util.List;

public interface IOrderDao extends JpaRepository<Order, Integer> {
	// 依據會員ID查詢歷史訂單(按日期降序)
	List<Order> findByMemberIdOrderByOrderIdDesc(Integer memberId);

	// 管理員查詢：所有訂單按日期升序
	List<Order> findAllByOrderByOrderDateAsc();

	// 用SQL計算購物車總額
	@Query("SELECT SUM(c.productPrice * c.quantity) FROM CartItem c WHERE c.cartId = :cartId")
	BigDecimal getCartTotal(@Param("cartId") Integer cartId);
	
	// 模糊查詢收件人名字 (orderName) 或關聯會員名字
	List<Order> findByOrderNameContainingOrderByOrderDateAsc(String orderName);
}
