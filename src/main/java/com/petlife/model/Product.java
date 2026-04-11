package com.petlife.model;

import java.io.Serializable;

import jakarta.persistence.*;

@Entity
@Table(name = "Product")
public class Product implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//商品編號(主鍵)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Integer productId;
	
	// 商品分類編號 (外鍵)
	@Column(name = "category_id")
	private Integer categoryId;
	
	// 商品名稱
	@Column(name = "product_name" , nullable = false , length = 100)
	private String productName;
	
	// 商品價格
	@Column(name = "product_price" , nullable = false)
	private Integer productPrice;
	
	// 商品描述
	@Column(name = "product_description" , columnDefinition = "TEXT", length = 2000)
	private String productDescription;
	
	// 商品圖片
	@Column(name = "product_image" , length = 255)
	private String productImage;
	
	// 商品庫存
	@Column(name = "product_stock")
	private Integer productStock;
	
	// 低庫存預警門檻
	@Column(name = "low_stock")
	private Integer lowStock;
	
	// 庫存儲位資訊
	@Column(name = "storage_position")
	private String storagePosition;
	
	// 狀態 (1=上架, 0=下架)
	@Column(name = "product_status")
	private Integer productStatus;
	
	// 關聯分類名稱 (非資料庫欄位，用於 join 顯示)
    @Transient
    private String categoryName;
    
    // Getter / Setter
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getProductPrice() { return productPrice; }
    public void setProductPrice(Integer productPrice) { this.productPrice = productPrice; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public Integer getProductStock() { return productStock; }
    public void setProductStock(Integer productStock) { this.productStock = productStock; }

    public Integer getLowStock() { return lowStock; }
    public void setLowStock(Integer lowStock) { this.lowStock = lowStock; }

    public String getStoragePosition() { return storagePosition; }
    public void setStoragePosition(String storagePosition) { this.storagePosition = storagePosition; }

    public Integer getProductStatus() { return productStatus; }
    public void setProductStatus(Integer productStatus) { this.productStatus = productStatus; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

	
}
