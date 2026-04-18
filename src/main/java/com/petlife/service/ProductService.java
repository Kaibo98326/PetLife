package com.petlife.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petlife.model.Product;
import com.petlife.repository.ProductRepository;

@Service
@Transactional 
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

//===== 查全部商品 (不分頁) ====================================================================================
    @Transactional(readOnly = true) // 查詢操作設定為唯讀，優化效能
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

//===== 依 ID 查商品 ========================================================================================
    @Transactional(readOnly = true) // 查詢操作設定為唯讀，優化效能
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

//===== 新增&更新商品 ========================================================================================
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

//===== 刪除商品 ============================================================================================
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

//===== 依分類查商品 (分頁) ===================================================================================
    @Transactional(readOnly = true) // 查詢操作設定為唯讀，優化效能
    public Page<Product> getProductsByCategory(Integer categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);	// Spring Data JPA 的頁碼是從 0 開始，所以畫面傳進來的 page 要減 1
        return productRepository.findByCategory(categoryId, pageable);
    }

//===== 依關鍵字查商品 (分頁) ==================================================================================
    @Transactional(readOnly = true) // 查詢操作設定為唯讀，優化效能
    public Page<Product> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.searchByName(keyword, pageable);
    }
    
//===== 獲取低庫存商品清單 =========================================================================

    @Transactional(readOnly = true)
    public Page<Product> getLowStockProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.findLowStock(pageable);
    }

//===== 獲取低庫存商品總數 ===============================================================================

    @Transactional(readOnly = true)
    public long getLowStockCount() {
        return productRepository.countLowStock();
    }    
    
}