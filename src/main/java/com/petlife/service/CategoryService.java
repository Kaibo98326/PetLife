package com.petlife.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petlife.model.Category;
import com.petlife.repository.CategoryRepository;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

//===== 查詢所有分類 =========================================================================================
    @Transactional(readOnly = true) // 查詢操作設定為唯讀，優化效能
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

//===== 查詢單筆分類 =========================================================================================
    @Transactional(readOnly = true)	// 查詢操作可設定為唯讀，優化效能
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

//===== 新增分類 ============================================================================================
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

//===== 修改分類 ============================================================================================
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

//===== 刪除分類 ============================================================================================
    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}