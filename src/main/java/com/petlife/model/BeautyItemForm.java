package com.petlife.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BeautyItemForm {

    private Integer beautyId;
    private String itemName;
    private String description;
    private BigDecimal smallPrice;
    private BigDecimal mediumPrice;
    private BigDecimal largePrice;
}
