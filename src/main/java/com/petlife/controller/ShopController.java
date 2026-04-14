package com.petlife.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.petlife.model.Category;
import com.petlife.model.Product;
import com.petlife.service.CartService;
import com.petlife.service.CategoryService;
import com.petlife.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/shop")
public class ShopController {
	
	 	@Autowired
	    private ProductService productService;

	    @Autowired
	    private CartService cartService; 
	    
	    @Autowired
	    private CategoryService categoryService;

	    //@Autowired
	    //private CartItemService cartItemService; // 取代原本的 CartItemDao

	    // --- 首頁 ---
	    @GetMapping("/index")
	    public String index(@RequestParam(value = "catId", required = false) Integer catId,
	                        @RequestParam(value = "keyword", required = false) String keyword,
	                        @RequestParam(value = "cp", defaultValue = "1") int cp,
	                        HttpSession session,
	                        Model model) {
	    		model.addAttribute("memberId", session.getAttribute("memberId"));
	    		model.addAttribute("memberName" , session.getAttribute("memberName"));
	        // 1. 計算購物車總件數  115.4.13修改
	        Integer memberId = (Integer) session.getAttribute("memberId");
	        if (memberId == null) memberId = 1; // 測試用預設
	        Integer totalQty = cartService.getCartTotalQuantity(memberId);
	        model.addAttribute("cartTotalQty", totalQty);

	        // 2. 取得所有分類 (左側選單)
	        List<Category> categories = categoryService.getAllCategory();
	        model.addAttribute("category", categories);

	        // 3. 商品查詢邏輯
	        List<Product> products;
	        String searchMsg = "";

	        if (keyword != null && !keyword.trim().isEmpty()) {
	            // 【B. 關鍵字搜尋模式】
	            String cleanKeyword = keyword.trim();
	            products = productService.searchProducts(cleanKeyword, 1, 100).stream()
	                    .filter(p -> p.getProductStatus() == 1)
	                    .collect(Collectors.toList());
	            searchMsg = "搜尋關鍵字：「" + cleanKeyword + "」";
	            model.addAttribute("keyword", cleanKeyword);

	        } else if (catId != null) {
	            // 【A. 分類篩選模式】
	            products = productService.getProductsByCategory(catId, 1, 100).stream()
	                    .filter(p -> p.getProductStatus() == 1)
	                    .collect(Collectors.toList());
	            searchMsg = "商品分類結果";
	            model.addAttribute("catId", catId);

	        } else {
	            // 【C. 預設模式】
	            products = getSafeAllActiveProducts();
	        }

	        // 4. 封裝結果送往 JSP
	        model.addAttribute("products", products);
	        model.addAttribute("searchMsg", searchMsg);

	        return "shop"; // 對應 Thymeleaf 模板 (原本的 /Jsp/Shop.jsp)
	    }

	    // --- 安全取得所有上架商品 ---
	    private List<Product> getSafeAllActiveProducts() {
	        List<Product> all = productService.getAllProducts();
	        if (all == null) return List.of();
	        return all.stream()
	                .filter(p -> p != null && p.getProductStatus() == 1)
	                .collect(Collectors.toList());
	    }

}
