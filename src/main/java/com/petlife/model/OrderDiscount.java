package com.petlife.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "OrderDiscount")
@IdClass(OrderDiscountId.class) // 綁定複合主鍵類別
@Getter
@Setter
public class OrderDiscount {

    // 這裡我們暫時將 order_id 設為單純的欄位，如果你已經有 Order 實體，可以改為 @ManyToOne
    @Id
    @Column(name = "order_id")
    private Integer orderId;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private Discount discount;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "product_price", precision = 10, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    // Hibernate 必須的無參數建構子
    public OrderDiscount() {
    }

    // 依據你要求的參數建構子
    public OrderDiscount(Integer orderId, Discount discount, Integer quantity, 
                         BigDecimal productPrice, BigDecimal discountAmount, BigDecimal subtotal) {
        this.orderId = orderId;
        this.discount = discount;
        this.quantity = quantity;
        this.productPrice = productPrice;
        this.discountAmount = discountAmount;
        this.subtotal = subtotal;
    }
}