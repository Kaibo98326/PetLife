package com.petlife.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BeautyItem")
@Getter
@Setter
@NoArgsConstructor
public class BeautyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "beauty_id")
    private Integer beautyId;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "beautyItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BeautyItemPrice> priceList = new ArrayList<>();

    public void addPrice(BeautyItemPrice price) {
        price.setBeautyItem(this);
        this.priceList.add(price);
    }

    public void clearPrices() {
        for (BeautyItemPrice price : priceList) {
            price.setBeautyItem(null);
        }
        priceList.clear();
    }

    public BigDecimal getSmallPrice() {
        return resolvePrice("小型");
    }

    public BigDecimal getMediumPrice() {
        return resolvePrice("中型");
    }

    public BigDecimal getLargePrice() {
        return resolvePrice("大型");
    }

    private BigDecimal resolvePrice(String petSize) {
        if (priceList == null) {
            return null;
        }
        return priceList.stream()
                .filter(price -> petSize.equals(price.getPetSize()))
                .map(BeautyItemPrice::getItemPrice)
                .findFirst()
                .orElse(null);
    }
}
