package com.petlife.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	// --- 準備新增頁面 ---
    @GetMapping("/prepareAdd")
    public String prepareAdd(Model model) {
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("allCategories", categories);
        return "InnerProductAddJSP";
    }

    // --- 查詢單筆詳細資料 (唯讀模式) ---
    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") Integer id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "InnerProductDetailViewJSP";
    }

    // --- 查詢單筆詳細資料 (彈窗用) ---
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "InnerProductDetailJSP";
    }

    // --- 修改頁面 (帶入舊資料與分類選單) ---
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id,
                       @RequestParam(value = "cp", defaultValue = "1") int cp,
                       Model model) {
        Product product = productService.getProductById(id);
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("product", product);
        model.addAttribute("allCategories", categories);
        model.addAttribute("currentPage", cp);
        return "InnerProductEditJSP";
    }

    // --- 查詢分頁列表、關鍵字搜尋與分類篩選 ---
    @GetMapping("/list")
    public String list(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                       @RequestParam(value = "searchKeyword", required = false, defaultValue = "") String keyword,
                       @RequestParam(value = "cp", defaultValue = "1") int cp,
                       Model model) {

        int pageSize = 10;
        List<Product> list;
        int totalRecords;

        if (categoryId != null) {
            list = productService.getProductsByCategory(categoryId, cp, pageSize);
            totalRecords = productService.countByCategory(categoryId);
            model.addAttribute("CategoryId", categoryId);
        } else {
            list = productService.searchProducts(keyword, cp, pageSize);
            totalRecords = productService.countAll(keyword);
        }

        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        model.addAttribute("productList", list);
        model.addAttribute("currentPage", cp);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("searchKeyword", keyword);

        return "InnerProductJSP";
    }
    
 // --- 按分類篩選商品列表 ---
    @GetMapping("/listByCategory")
    public String listByCategory(@RequestParam("categoryId") Integer categoryId,
                                 @RequestParam(value = "cp", defaultValue = "1") int cp,
                                 Model model) {
        int pageSize = 10;
        List<Product> list = productService.getProductsByCategory(categoryId, cp, pageSize);
        int totalRecords = productService.countByCategory(categoryId);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        model.addAttribute("productList", list);
        model.addAttribute("currentPage", cp);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("categoryId", categoryId);

        return "InnerProductJSP";
    }

    // --- 刪除商品 ---
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        productService.deleteProduct(id);
        return "redirect:/innerProduct/list";
    }

    // --- 新增商品 (含圖片上傳) ---
    @PostMapping("/insert")
    public String insertProduct(@RequestParam("productName") String productName,
                                @RequestParam("categoryId") Integer categoryId,
                                @RequestParam("productPrice") Integer productPrice,
                                @RequestParam("productStock") Integer productStock,
                                @RequestParam("lowStock") Integer lowStock,
                                @RequestParam("storagePosition") String storagePosition,
                                @RequestParam("productDescription") String productDescription,
                                @RequestParam("productStatus") Integer productStatus,
                                @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Product product = new Product();
            product.setProductName(productName);
            product.setCategoryId(categoryId);
            product.setProductPrice(productPrice);
            product.setProductStock(productStock);
            product.setLowStock(lowStock);
            product.setStoragePosition(storagePosition);
            product.setProductDescription(productDescription);
            product.setProductStatus(productStatus);

            String fileName = "test.jpg"; // 預設圖片
            if (file != null && !file.isEmpty()) {
                fileName = file.getOriginalFilename();
                String savePath = "src/main/resources/static/image";
                java.io.File saveFile = new java.io.File(savePath, fileName);
                file.transferTo(saveFile);
            }
            product.setProductImage(fileName);

            productService.addProduct(product);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/innerProduct/list";
    }

    // --- 修改商品 (含判斷是否更換圖片) ---
    @PostMapping("/update")
    public String updateProduct(@RequestParam("productId") Integer productId,
                                @RequestParam("productName") String productName,
                                @RequestParam("categoryId") Integer categoryId,
                                @RequestParam("productPrice") Integer productPrice,
                                @RequestParam("productStock") Integer productStock,
                                @RequestParam("lowStock") Integer lowStock,
                                @RequestParam("storagePosition") String storagePosition,
                                @RequestParam("productDescription") String productDescription,
                                @RequestParam("productStatus") Integer productStatus,
                                @RequestParam("oldImage") String oldImage,
                                @RequestParam(value = "cp", defaultValue = "1") int cp,
                                @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Product product = new Product();
            product.setProductId(productId);
            product.setProductName(productName);
            product.setCategoryId(categoryId);
            product.setProductPrice(productPrice);
            product.setProductStock(productStock);
            product.setLowStock(lowStock);
            product.setStoragePosition(storagePosition);
            product.setProductDescription(productDescription);
            product.setProductStatus(productStatus);

            // 判斷是否有新圖片
            String fileName = oldImage;
            if (file != null && !file.isEmpty()) {
                fileName = file.getOriginalFilename();
                String savePath = "src/main/resources/static/image"; // Spring Boot 靜態資源路徑
                java.io.File saveFile = new java.io.File(savePath, fileName);
                file.transferTo(saveFile);
            }
            product.setProductImage(fileName);

            productService.updateProduct(product);

            return "redirect:/innerProduct/list?cp=" + cp;

        } catch (Exception e) {
            e.printStackTrace();
            return "errorPage";
        }
    }


	
}
