package com.petlife.model;

import java.io.Serializable;

import org.hibernate.annotations.Formula;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 							// 宣告這是一個資料庫實體
@Table(name = "Category") 		// 對應 SQL Server 的資料表名稱
@Data 							// 自動產生 Getter, Setter, toString, equals, hashCode
@NoArgsConstructor 				// 自動產生無參數建構子
@AllArgsConstructor 				// 自動產生全參數建構子

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

//商品分類編號 (主鍵)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

//商品分類名稱
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

//商品數量 (非資料庫欄位，用於查詢統計)
 // Hibernate 的動態計算 @Formula
    @Formula("(SELECT COUNT(*) FROM Product p WHERE p.category_id = category_id)")
    private Integer productCount;
}

