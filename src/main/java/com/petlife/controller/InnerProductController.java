package com.petlife.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.petlife.model.Category;
import com.petlife.model.Product;
import com.petlife.service.CategoryService;
import com.petlife.service.ProductService;

@Controller
@RequestMapping("/innerProduct")
public class InnerProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;
    
//===== 查詢商品列表 (支援分頁、搜尋、分類篩選) ================================================================================================

    @GetMapping("/list")
    public String list(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                       @RequestParam(value = "searchKeyword", defaultValue = "") String keyword,
                       @RequestParam(value = "cp", defaultValue = "1") int cp,
                       Model model) {
        int pageSize = 10;
        Page<Product> productPage;
        
        if (categoryId != null && categoryId != 0) {
            productPage = productService.getProductsByCategory(categoryId, cp, pageSize);
            model.addAttribute("categoryId", categoryId);
        } else {
            productPage = productService.searchProducts(keyword, cp, pageSize);
            model.addAttribute("searchKeyword", keyword);
        }

        // --- 手動補齊分類名稱 ---
        List<Product> productList = productPage.getContent();
        for (Product p : productList) {
            // 如果這項商品有分類 ID
            if (p.getCategoryId() != null) {
                // 利用 categoryService 去查出該分類的完整物件
                Category cat = categoryService.getCategoryById(p.getCategoryId());
                if (cat != null) {
                    // 把查到的名稱塞進那個 @Transient 的 categoryName 欄位
                    p.setCategoryName(cat.getCategoryName());
                }
            }
        }

        model.addAttribute("productList", productList);
        model.addAttribute("currentPage", cp);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "InnerProduct";
    }
//===== 後台商品 顯示新增商品表單 (進入準備頁面) ================================================================================================

    @GetMapping("/prepareAdd")
    public String prepareAdd(Model model) {
        model.addAttribute("allCategories", categoryService.getAllCategory());
        return "InnerProductAdd";
    }

//===== 後台商品 新增商品 ================================================================================================
 
    @PostMapping("/insert")
    @ResponseBody 
    public String insertProduct(Product product, @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            handleImageUpload(product, file, "test.jpg");
            productService.addProduct(product);
            return "success";
        } catch (Exception e) { return "fail"; }
    }

//===== 後台商品 刪除商品 ================================================================================================

    @GetMapping("/delete/{id}")
    @ResponseBody
    public String delete(@PathVariable("id") Integer id) {
        try {
            productService.deleteProduct(id);
            return "success";
        } catch (Exception e) { return "fail"; }
    }
    
//===== 後台商品 顯示商品編輯表單 (載入舊資料供使用者編輯) ================================================================================================

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id,
                       @RequestParam(value = "cp", defaultValue = "1") int cp,
                       Model model) {
        // 💡 根據 ID 抓取商品資料
        Product product = productService.getProductById(id);
        // 💡 抓取所有分類給下拉選單選
        List<Category> categories = categoryService.getAllCategory();
        
        model.addAttribute("product", product);
        model.addAttribute("allCategories", categories);
        model.addAttribute("currentPage", cp); // 紀錄目前頁碼，修改完可以跳回去
        
        return "InnerProductEdit";
    }
//===== 後台商品 執行修改商品 (處理新舊圖片替換並更新資料庫) ================================================================================================

    @PostMapping("/update")
    @ResponseBody
    public String updateProduct(Product product,
                                @RequestParam("oldImage") String oldImage,
                                @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            // 💡 處理圖片：有新傳新，沒新用舊
            handleImageUpload(product, file, oldImage);
            productService.updateProduct(product);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

//===== 後台商品 工具：處理圖片上傳與路徑存儲 ================================================================================================

    private void handleImageUpload(Product product, MultipartFile file, String defaultImage) throws Exception {
    		//外部儲存目錄
    		String uploadDir = "C:/uploads/images/products/";
    		java.io.File directory = new java.io.File(uploadDir);
    		if(!directory.exists()) {
    			directory.mkdirs();
    		}
    		
        if (file != null && !file.isEmpty()) {
        		//取得原始檔名
            String fileName = file.getOriginalFilename();
            //儲存檔案到外部目錄
            file.transferTo(new java.io.File(uploadDir,fileName));
            
            //儲存資料庫的路徑:images/xxx
            product.setProductImage("images/products/" + fileName);
        } else { product.setProductImage(defaultImage); }
    }
}
