package com.petlife.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petlife.model.Discount;
//資料庫層
@Repository
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    // 繼承 JpaRepository 後，就自動擁有了基本的 CRUD (新增、查詢、修改、刪除) 功能
    // 以後如果需要特殊的查詢，例如「搜尋活動名稱」，可以寫在這裡：
    // List<Discount> findByDiscountNameContaining(String keyword);
}