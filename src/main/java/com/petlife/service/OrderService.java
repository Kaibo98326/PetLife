package com.petlife.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.petlife.model.OrderDetail;
import com.petlife.model.Product;
import com.petlife.model.CartItem;
import com.petlife.model.Order;
import com.petlife.repository.IOrderDao;
import com.petlife.repository.IOrderDetailDao;
import com.petlife.repository.ProductRepository;

@Service
@Transactional
public class OrderService {

	@Autowired
	private IOrderDao orderDao;
	@Autowired
	private IOrderDetailDao detailDao;
	@Autowired
	private CartService cartService;
	@Autowired
	private ProductRepository pr;

	public Integer createOrderFromCart(Integer memberId, Integer cartId, String name, String phone, String addr,
	        String payment, String note) {

	    List<CartItem> cartItems = cartService.getCartItems(memberId);
	    if (cartItems == null || cartItems.isEmpty())
	        throw new RuntimeException("購物車空了");

	    // 建立Order實體並設定基本資料
	    Order order = new Order();
	    order.setMemberId(memberId);
	    order.setOrderName(name);
	    order.setOrderPhone(phone);
	    order.setOrderAddress(addr);
	    order.setOrderPayment(payment);
	    order.setOrderNote(note);
	    order.setOrderTotal(cartService.calculateCartTotal(memberId));

	    // 處理明細與庫存
	    for (CartItem ci : cartItems) {
	        // 更新商品庫存
	        Product product = pr.findById(ci.getProductId())
	                .orElseThrow(() -> new RuntimeException("找不到商品ID: " + ci.getProductId()));
	        product.setProductStock(product.getProductStock() - ci.getQuantity());

	        // 建立明細實體
	        OrderDetail detail = new OrderDetail();
	        detail.setOrderBean(order); // 設定雙向關聯
	        detail.setProductId(ci.getProductId());
	        detail.setQuantity(ci.getQuantity());
	        detail.setProductPrice(ci.getProductPrice()); // 對應 OrderDetail 欄位
	        detail.setProductName(product.getProductName()); // 必須塞值，因為 @NonNull
	        
	        // 計算小計
	        java.math.BigDecimal subtotal = ci.getProductPrice().multiply(new java.math.BigDecimal(ci.getQuantity()));
	        detail.setSubtotal(subtotal);

	        // 重要：將明細加入 Order 的 List 中
	        order.getDetails().add(detail);
	    }

	    // 執行存檔：這一步會因為 CascadeType.ALL 同步儲存 OrderDetail
	    Order savedOrder = orderDao.save(order);

	    // 清空購物車
	    cartService.clearCart(cartId);

	    // 回傳剛剛產生的ID
	    return savedOrder.getOrderId();
	}
	
	// 讓結帳畫面有東西顯示
	public Order getOrderMain(Integer orderId) {
		return orderDao.findById(orderId).orElse(null);
	}

	public List<OrderDetail> getOrderItems(Integer orderId) {
		return detailDao.findByOrderBean_OrderId(orderId);
	}
}