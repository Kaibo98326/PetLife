package com.petlife.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BeautyRecord")
@Getter
@Setter
@NoArgsConstructor
public class BeautyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @Column(name = "pet_id", nullable = false)
    private Integer petId;

    @Column(name = "beauty_date", nullable = false)
    private LocalDate beautyDate;

    @Column(name = "beauty_slot", nullable = false, length = 50)
    private String beautySlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beauty_id", nullable = false)
    private BeautyItem beautyItem;

    @Column(name = "pet_size", nullable = false, length = 20)
    private String petSize;

    @Column(name = "item_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal itemPrice;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    public String getItemName() {
        return beautyItem != null ? beautyItem.getItemName() : null;
    }

    public Integer getBeautyId() {
        return beautyItem != null ? beautyItem.getBeautyId() : null;
    }
}
