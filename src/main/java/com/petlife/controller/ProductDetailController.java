package com.petlife.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import com.petlife.model.Product;
import com.petlife.model.Category; // 💡 記得匯入 Category
import com.petlife.service.ProductService;
import com.petlife.service.CategoryService; // 💡 記得匯入 CategoryService
import com.petlife.service.CartService;

@Controller
public class ProductDetailController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CartService cartService;

//===== 前台商城 顯示商品詳情頁面 (含購物車狀態) ================================================================================================

	@GetMapping("/product/detail")
	public String getProductDetail(@RequestParam("id") Integer id, HttpSession session, Model model) {

		// --- 處理導覽列：獲取目前會員的購物車總件數 ---
		Integer memberId = (Integer) session.getAttribute("memberId");
		String memberName = (String) session.getAttribute("memberName");
		// if (memberId == null) {
		// memberId = 1; // 測試用預設值，實際上線後可視需求移除
		// }
		System.out.println("Debug - MemberID in Session: " + memberId);
	    System.out.println("Debug - MemberName in Session: " + memberName);
		// --- 會員資訊傳給 Model，導覽列才抓得到名字 ---
		model.addAttribute("memberId", memberId);
		model.addAttribute("memberName", session.getAttribute("memberName"));
		// --- 也要傳購物車數量 ---
		model.addAttribute("cartTotalQty",
				session.getAttribute("cartTotalQty") != null ? session.getAttribute("cartTotalQty") : 0);

		// --- 若已登入會員，則即時查詢最新購物車數量 ---
		if (memberId != null) {
			int totalQty = cartService.getTotalQuantityByMemberId(memberId);
			model.addAttribute("cartTotalQty", totalQty);
		}

		// --- 若無商品 ID 則導回商城首頁 ---
		if (id == null) {
			return "redirect:/shop/index"; // 💡 路徑修正為我們統一的 /shop/index
		}
		// --- 取得商品資料 ---
		Product product = productService.getProductById(id);

		if (product != null) {
			// --- 手動關聯分類名稱 (處理 @Transient 欄位) ---
			if (product.getCategoryId() != null) {
				// 根據商品存放的分類 ID 找尋對應名稱
				Category cat = categoryService.getCategoryById(product.getCategoryId());
				if (cat != null) {
					// 將查詢結果塞入非持久化欄位，供前端 Thymeleaf 顯示
					product.setCategoryName(cat.getCategoryName());
				}
			}
			model.addAttribute("product", product);
			return "ProductDetail";
		} else {
			return "redirect:/shop/index";
		}
	}
}