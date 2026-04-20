package com.petlife.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petlife.model.BeautyItemPrice;

public interface BeautyItemPriceRepository extends JpaRepository<BeautyItemPrice, Integer> {

    Optional<BeautyItemPrice> findByBeautyItemBeautyIdAndPetSize(Integer beautyId, String petSize);
}
