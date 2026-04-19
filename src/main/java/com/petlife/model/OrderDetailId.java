package com.petlife.model;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailId implements Serializable {
    private static final long serialVersionUID = 1L;

    // 這裡的變數名必須與 OrderDetail 類別中標註 @Id 的變數名一致
    private Order orderBean; // 對應 Order 的 ID
    private Integer productId; // 對應產品 ID
}
