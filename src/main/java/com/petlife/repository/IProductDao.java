package com.petlife.repository;



import java.util.List;


import com.petlife.model.Product;

public interface IProductDao {
	
	Product selectById(Integer productId);
	
	List<Product> selectAll();
	
	List<Product> selectByCategory(Integer categoryId , int page , int size);
	
	List<Product> selectByKeyword(String keyword , int page , int size);
	
	String insert(Product product);
	
	Product update(Product product);
	
	boolean deleteById(Integer id);
	
	int countAll(String keyword);
	
	int countByCategory(Integer categoryId);
}
