package com.petlife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petlife.model.Category;
import com.petlife.repository.ICategoryDao;

@Service
public class CategoryService {
	private final ICategoryDao categoryDao;

    @Autowired
    public CategoryService(ICategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    // 查全部分類
    public List<Category> getAllCategory() {
        return categoryDao.selectAll();
    }

    // 查單筆分類
    public Category getCategoryById(Integer id) {
        return categoryDao.selectById(id);
    }

    // 新增分類
    public String addCategory(Category category) {
        return categoryDao.insert(category);
    }

    // 修改分類
    public Category updateCategory(Category category) {
        return categoryDao.update(category);
    }

    // 刪除分類
    public boolean deleteCategory(Integer id) {
        return categoryDao.deleteById(id);
    }

}
