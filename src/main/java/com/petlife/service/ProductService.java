package com.petlife.service;

import com.petlife.repository.IProductDao;
import com.petlife.model.Product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProductService {
	
	private final IProductDao productDao;
	
	@Autowired
	public ProductService(IProductDao productDao) {
		this.productDao = productDao;
	}
	
	// 查全部商品
	public List<Product> getAllProducts(){
		return productDao.selectAll();
	}
	// 依 ID 查商品
    public Product getProductById(Integer id) {
        return productDao.selectById(id);
    }

    // 新增商品
    public String addProduct(Product product) {
        return productDao.insert(product);
    }

    // 更新商品
    public Product updateProduct(Product product) {
        return productDao.update(product);
    }

    // 刪除商品
    public boolean deleteProduct(Integer id) {
        return productDao.deleteById(id);
    }

    // 依分類查商品 (分頁)
    public List<Product> getProductsByCategory(Integer categoryId, int page, int size) {
        return productDao.selectByCategory(categoryId, page, size);
    }

    // 依關鍵字查商品 (分頁)
    public List<Product> searchProducts(String keyword, int page, int size) {
        return productDao.selectByKeyword(keyword, page, size);
    }

    // 計算總筆數 (搜尋用)
    public int countAll(String keyword) {
        return productDao.countAll(keyword);
    }

    // 計算某分類商品總筆數
    public int countByCategory(Integer categoryId) {
        return productDao.countByCategory(categoryId);
    }

}
