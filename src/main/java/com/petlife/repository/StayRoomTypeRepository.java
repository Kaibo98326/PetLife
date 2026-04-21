package com.petlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petlife.model.StayRoomType;

@Repository
public interface StayRoomTypeRepository extends JpaRepository<StayRoomType, Integer>{

}
