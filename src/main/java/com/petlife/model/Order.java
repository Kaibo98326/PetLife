package com.petlife.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

import org.springframework.stereotype.Component;

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
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Order")
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

	@Id  @Column(name = "order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderId;
	
	@Column(name = "member_id")
	private Integer memberId;
	
	@Column(name = "order_name") @NonNull
	private String orderName;
	@Column(name = "order_phone") @NonNull
	private String orderPhone;
	@Column(name = "order_date") 
	private LocalDateTime orderDate;
	@Column(name = "order_total") @NonNull
	private BigDecimal orderTotal;
	@Column(name = "order_status") 
	private String orderStatus;
	@Column(name = "order_note") 
	private String orderNote;
	@Column(name = "order_address") @NonNull
	private String orderAddress;
	@Column(name = "order_payment") @NonNull
	private String orderPayment;

	// 雙向關聯設定
    // mappedBy 必須對應 OrderDetailBean 裡的變數名稱 "orderBean"
    @OneToMany(mappedBy = "orderBean", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // 防止 Lombok 造成的無限遞迴
    private List<OrderDetail> details = new ArrayList<>();

    // --- 自動化處理邏輯 ---

    @PrePersist
    protected void onCreate() {
        // 自動填入時間，取代GETDATE()
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
        // 設定預設狀態
        if (this.orderStatus == null) {
            this.orderStatus = "待處理";
        }
    }
}