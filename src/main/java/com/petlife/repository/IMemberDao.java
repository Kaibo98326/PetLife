package com.petlife.repository;


import java.util.List;
import java.util.Optional;
import com.petlife.model.Member;

public interface IMemberDao {
	
	// 依照主鍵查詢會員
    Optional<Member> findById(Integer memberId);

    // 依照 email 查詢會員
    Optional<Member> findByEmail(String email);

    // 查詢所有會員
    List<Member> findAll();

    // 新增或更新會員
    Member save(Member member);

    // 軟刪除會員 (改狀態欄位，不刪資料)
    boolean softDelete(Integer memberId);
    
    // 停權會員
    boolean disable(Integer memberId); 






	
}
