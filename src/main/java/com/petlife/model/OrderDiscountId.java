package com.petlife.model;

import java.io.Serializable;
import java.util.Objects;

public class OrderDiscountId implements Serializable {
    private Integer orderId;
    private Integer discount; // 對應實體中的屬性名稱

    public OrderDiscountId() {
    }

    public OrderDiscountId(Integer orderId, Integer discount) {
        this.orderId = orderId;
        this.discount = discount;
    }

    // 複合主鍵必須實作 equals 和 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDiscountId that = (OrderDiscountId) o;
        return Objects.equals(orderId, that.orderId) &&
               Objects.equals(discount, that.discount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, discount);
    }
}