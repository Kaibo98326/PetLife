package com.petlife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.petlife.model.Category;
import com.petlife.service.CategoryService;

@Controller
@RequestMapping("/innerCategory")
public class InnerCategoryController {

    @Autowired
    private CategoryService categoryService;
    
//===== 查詢 所有分類列表 ================================================================================================

    @GetMapping("/list")
    public String list(Model model) {
        List<Category> categoryList = categoryService.getAllCategory();
        // model.addAttribute 就等同於原本的 request.setAttribute
        model.addAttribute("category", categoryList);
        
        // 回傳 View 的名稱 (Spring 會自動對應到 /WEB-INF/views/...InnerCategoryJSP.jsp)
        return "InnerCategory";	// 對應 templates/InnerCategory.html
    }

//===== 後台分類 顯示修改編輯頁面 (讀取舊資料並開啟彈窗/頁面) ================================================================================================

    @GetMapping("/edit")
    public String edit(@RequestParam("id") Integer id, Model model) {
        Category category = categoryService.getCategoryById(id);
        model.addAttribute("item", category);
        return "InnerCategoryEdit";
    }
    
//===== 後台分類 執行更新分類 (將修改後的資料寫入資料庫) ================================================================================================
    
    @PostMapping("/update")
    @ResponseBody
    public String update(@RequestParam("categoryId") Integer id,
    		@RequestParam("categoryName") String name) {
    	try {
    		Category category = new Category();
    		category.setCategoryId(id);
    		category.setCategoryName(name);
    		categoryService.updateCategory(category);
    		return "success";
    	} catch (Exception e) {
    		e.printStackTrace();
    		return "fail";
    	}
    }

//===== 後台分類 新增分類 ================================================================================================

    @PostMapping("/insert")
    @ResponseBody
    public String insert(@RequestParam("categoryName") String name) {
        if (name != null && !name.trim().isEmpty()) {
            Category category = new Category();
            category.setCategoryName(name);
            categoryService.addCategory(category);
            return "success";
        }
        return "fail";
    }

//===== 後台分類 刪除分類 ================================================================================================

    @GetMapping("/delete")
    @ResponseBody
    public String delete(@RequestParam("id") Integer id) {
        try {
            categoryService.deleteCategory(id);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}