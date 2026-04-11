package com.petlife.repository;

import java.util.List;

import com.petlife.model.Category;

public interface ICategoryDao {
	Category selectById(Integer categoryId);

    List<Category> selectAll();

    String insert(Category category);

    Category update(Category category);

    boolean deleteById(Integer id);


}
