package com.petlife.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Id;
import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity 
@Table(name="cartItem")
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;
	
    @Transient
	private Product product; // 這樣一條購物車明細就能直接帶出所有商品資訊
 	
	@Id @Column(name="item_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer itemId;
	
	@Column(name="cart_id")
    private Integer cartId;    // 屬性：屬於哪台購物車
	
	@Column(name="product_id")
    private Integer productId; // 屬性：什麼商品
	
	@Column(name = "product_name") @NonNull
    private String productName;
	
	@Column(name = "product_price") @NonNull
    private BigDecimal productPrice;
	
	@Column(name = "quantity") @NonNull
    private Integer quantity;
	
	@Column(name = "discount_amount")
    private BigDecimal discountAmount;
	
	@Column(name = "subtotal", insertable = false, updatable = false) @NonNull
    private BigDecimal subtotal;
}
