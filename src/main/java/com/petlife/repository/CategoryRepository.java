package com.petlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.petlife.model.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}

// findById, findAll, save, deleteById