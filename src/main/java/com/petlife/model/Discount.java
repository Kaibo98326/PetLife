package com.petlife.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Discount")
@Getter
@Setter
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 對應 IDENTITY 自動遞增
    @Column(name = "discount_id")
    private Integer discountId;

    @Column(name = "discount_name", length = 100)
    private String discountName;

    @Column(name = "status", length = 20)
    private String status = "active"; // 預設值

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "discount_description", length = 2000)
    private String discountDescription;

    // 與 DiscountType 的多對一關聯
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_type_id")
    private DiscountType discountType;

    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "is_member")
    private Boolean isMember;

    @Column(name = "minimum_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minimumPurchaseAmount;

    // Hibernate 必須的無參數建構子
    public Discount() {
    }

    // 依據你要求的參數建構子 (不包含自動生成的 discountId)
    public Discount(String discountName, String status, LocalDate startDate, LocalDate endDate, 
                    String discountDescription, DiscountType discountType, BigDecimal discountValue, 
                    Boolean isMember, BigDecimal minimumPurchaseAmount) {
        this.discountName = discountName;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountDescription = discountDescription;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.isMember = isMember;
        this.minimumPurchaseAmount = minimumPurchaseAmount;
    }
}