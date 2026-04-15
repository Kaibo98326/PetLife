package com.petlife.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DiscountType")
@Getter
@Setter
public class DiscountType {

    @Id
    @Column(name = "discount_type_id")
    private Integer discountTypeId;

    @Column(name = "discount_type_name", length = 100)
    private String discountTypeName;

    // Hibernate 必須的無參數建構子
    public DiscountType() {
    }

    // 依據你要求的參數建構子
    public DiscountType(Integer discountTypeId, String discountTypeName) {
        this.discountTypeId = discountTypeId;
        this.discountTypeName = discountTypeName;
    }
}
