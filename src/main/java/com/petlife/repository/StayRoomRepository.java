package com.petlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petlife.model.StayRoom;

@Repository
public interface StayRoomRepository extends JpaRepository<StayRoom, Integer>{

}
