package com.petlife.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petlife.model.Stay;

@Repository
public interface StayRepository extends JpaRepository<Stay, Integer>{

	List<Stay> findByPet_Member_MemberId(Integer pet_Member_MemberId);
	
}
