package com.petlife.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

// ===== 【商城首頁】 前台主頁面 支援：1. 全部商品 2. 分類篩選 3. 關鍵字搜尋 ============================================
	    
	    @GetMapping({"/index", ""}) 
	    public String index(@RequestParam(value = "catId", required = false) Integer catId,
	                        @RequestParam(value = "keyword", required = false) String keyword,
	                        @RequestParam(value = "cp", defaultValue = "1") int cp,
	                        @RequestParam(value = "all", required = false) String all,
	                        HttpSession session,
	                        Model model) {
<<<<<<< HEAD
	    		model.addAttribute("memberId", session.getAttribute("memberId"));
	    		model.addAttribute("memberName" , session.getAttribute("memberName"));
	        // 1. 計算購物車總件數  115.4.13修改
	        Integer memberId = (Integer) session.getAttribute("memberId");
=======
	    	
	    	
// ===== 顯示 登入 會員名稱資訊 ==============================================================================
	    	
	        model.addAttribute("memberId", session.getAttribute("memberId"));
	        model.addAttribute("memberName", session.getAttribute("memberName"));
	        model.addAttribute("cartTotalQty", session.getAttribute("cartTotalQty") != null ? session.getAttribute("cartTotalQty") : 0);

	        // 1. 計算購物車總件數
/*	        Integer memberId = (Integer) session.getAttribute("memberId");
>>>>>>> main
	        if (memberId == null) memberId = 1; // 測試用預設
	        Integer totalQty = cartService.getCartTotalQuantity(memberId);
	        model.addAttribute("cartTotalQty", totalQty);
<<<<<<< HEAD

	        // 2. 取得所有分類 (左側選單)
=======
*/
	        
// ===== 取得所有分類 (左側 menu 選單) =======================================================================
	        
>>>>>>> main
	        List<Category> categories = categoryService.getAllCategory();
	        model.addAttribute("category", categories);
	        
// ===== 商品 搜尋 與 篩選邏輯 ==============================================================================
	        
	        int pageSize = 100; // 前台商城通常一次顯示較多商品，或走無限捲動
	        Page<Product> productPage;
	        String searchMsg = "";

	        if (keyword != null && !keyword.trim().isEmpty()) {
	// 【關鍵字搜尋】
	            String cleanKeyword = keyword.trim();
	            productPage = productService.searchProducts(cleanKeyword, cp, pageSize);
	            searchMsg = "搜尋關鍵字：「" + cleanKeyword + "」";
	            model.addAttribute("keyword", cleanKeyword);
	            
	        } else if (catId != null && catId != 0) {
	// 【分類篩選】
	            productPage = productService.getProductsByCategory(catId, cp, pageSize);
	            searchMsg = "商品分類結果";
	            model.addAttribute("catId", catId);
	            
	        } else {
	// 【預設模式：顯示所有上架商品】
	            productPage = productService.searchProducts("", cp, pageSize);
	            if ("true".equals(all)) {
	                searchMsg = "全部商品項目";
	                model.addAttribute("hideCarousel", true); // 傳送指令給前端：隱藏廣告
	            }
	        }
	        
// ===== 取得所有上架商品 =================================================================================

	        List<Product> activeProducts = productPage.getContent().stream()
	                .filter(p -> p.getProductStatus() != null && p.getProductStatus() == 1)
	                .collect(Collectors.toList());

	        for (Product p : activeProducts) {
	            if (p.getCategoryId() != null) {
	                Category cat = categoryService.getCategoryById(p.getCategoryId());
	                if (cat != null) p.setCategoryName(cat.getCategoryName());
	            }
	        }

// ===== 將資料傳往 Thymeleaf ===========================================================================

	        model.addAttribute("products", activeProducts);
	        model.addAttribute("searchMsg", searchMsg);
	        model.addAttribute("currentPage", cp);
	        model.addAttribute("totalPages", productPage.getTotalPages());
	        
	        return "shop";	// 回傳 templates/Shop.html
	        
	        
	        
}}
