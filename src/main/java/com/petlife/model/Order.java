package com.petlife.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Entity
@Table(name = "[Order]")
@Data
@NoArgsConstructor // 這是 JPA 必須要有的
@AllArgsConstructor
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "[order_id]")
    private Integer orderId;
    
    @Column(name = "member_id")@NonNull
    private Integer memberId;
    
    @Column(name = "[order_name]")@NonNull
    private String orderName;

    @Column(name = "[order_phone]")
    private String orderPhone;

    @Column(name = "[order_date]") 
    private LocalDateTime orderDate;

    @Column(name = "[order_total]")
    private BigDecimal orderTotal;

    @Column(name = "[order_status]") @NonNull
    private String orderStatus;

    @Column(name = "[order_note]") 
    private String orderNote;

    @Column(name = "[order_address]")
    private String orderAddress;

    @Column(name = "[order_payment]")
    private String orderPayment;

    @OneToMany(mappedBy = "orderBean", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OrderDetail> details = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.orderDate == null) this.orderDate = LocalDateTime.now();
        if (this.orderStatus == null) this.orderStatus = "處理中";
    }
}
