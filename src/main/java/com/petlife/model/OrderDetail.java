package com.petlife.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity 
@Table(name="OrderDetail")
@IdClass(OrderDetailId.class) // 連接複合主鍵類別
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") //複合主鍵
    @ToString.Exclude
    private Order orderBean; 
    
    @Id
    @Column(name="product_id") // 對應資料庫的 product_id
    private Integer productId;
    
    @Column(name="product_name") @NonNull
    private String productName;
    
    @Column(name = "quantity") @NonNull
    private Integer quantity;
    
    @Column(name="product_price")  
    private BigDecimal productPrice;
    
    @Column(name="discount_amount")  
    private BigDecimal discountAmount;
    
    @Column(name = "subtotal") @NonNull
    private BigDecimal subtotal;

	public void setOrderId(Integer orderId) {
		
	}

	public void setPrice(@NonNull BigDecimal productPrice2) {
		// TODO Auto-generated method stub
		
	}
}
