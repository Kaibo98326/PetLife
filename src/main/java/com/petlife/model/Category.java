package com.petlife.model;

import java.io.Serializable;

import jakarta.persistence.*;

@Entity
@Table(name = "Category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    // 商品分類編號 (主鍵)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    // 商品分類名稱
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    // 商品數量 (非資料庫欄位，用於查詢統計)
    @Transient
    private Integer productCount;

    // 空建構子
    public Category() {}

    // Getter / Setter
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Integer getProductCount() { return productCount; }
    public void setProductCount(Integer productCount) { this.productCount = productCount; }
}

