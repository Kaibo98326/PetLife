package com.petlife.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.petlife.model.Product;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	
//===== 分類查詢 ================================================================================================

	@Query("SELECT p FROM Product p WHERE p.categoryId = :catId ORDER BY p.productId DESC")
	Page<Product> findByCategory(@Param("catId") Integer categoryId, Pageable pageable);

    
//===== 關鍵字查詢 ==============================================================================================

	@Query("SELECT p FROM Product p WHERE p.productName LIKE %:kw% ORDER BY p.productId DESC")
    Page<Product> searchByName(@Param("kw") String keyword, Pageable pageable);
    
	
//===== 統計筆數 ===============================================================================================
    
	long countByCategoryId(Integer categoryId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.productName LIKE %:kw%")
    long countByKeyword(@Param("kw") String keyword);
    

//===== 庫存預警查詢 ======================================================================================
    
    @Query("SELECT p FROM Product p WHERE p.productStock <= p.lowStock ORDER BY p.productId DESC")
    Page<Product> findLowStock(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.productStock <= p.lowStock")
    long countLowStock();
    
//===== 後台商品 批次上下架處理 ======================================================================================
    
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.productStatus = :status WHERE p.productId IN :ids")
    void batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") Integer status);
    
}


