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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity @Table(name="OrderDiscount")
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderDiscount  implements Serializable {
    private static final long serialVersionUID = 1L;
	
    @Id  @Column(name = "order_id")
   	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;    // PK, FK
    
    @Column(name = "discount_id") @NonNull
    private Integer discountId; // PK, FK
    
    @Column(name = "quantity") @NonNull
    private Integer quantity;
    
    @Column(name = "product_price") @NonNull
    private BigDecimal productPrice;
   
    @Column(name = "discount_amount") @NonNull
    private BigDecimal discountAmount;
    
    @Column(name = "subtotal") @NonNull
    private BigDecimal subtotal;
}
