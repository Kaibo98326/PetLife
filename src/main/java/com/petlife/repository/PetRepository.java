package com.petlife.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.petlife.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer>{

}
