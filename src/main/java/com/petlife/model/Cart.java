package com.petlife.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity @Table(name="Cart")
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;
	
	@Id @Column(name="cart_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer cartId;	    
	
	@Column(name = "member_id") @NonNull
    private Integer memberId;
	
	@Column(name = "created_at") @NonNull
    private LocalDateTime createdAt;
	@Column(name = "updated_at") 
    private LocalDateTime updatedAt;
}
