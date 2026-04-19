package com.petlife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.petlife.model.BeautyItem;

public interface BeautyItemRepository extends JpaRepository<BeautyItem, Integer> {

    @EntityGraph(attributePaths = "priceList")
    List<BeautyItem> findAllByOrderByBeautyIdAsc();

    @EntityGraph(attributePaths = "priceList")
    Optional<BeautyItem> findByBeautyId(Integer beautyId);

    @EntityGraph(attributePaths = "priceList")
    List<BeautyItem> findByItemNameContainingOrderByBeautyIdAsc(String keyword);
}
