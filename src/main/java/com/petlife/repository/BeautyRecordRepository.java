package com.petlife.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.petlife.model.BeautyRecord;

public interface BeautyRecordRepository extends JpaRepository<BeautyRecord, Integer>, JpaSpecificationExecutor<BeautyRecord> {

    @EntityGraph(attributePaths = "beautyItem")
    Optional<BeautyRecord> findByRecordId(Integer recordId);

    @Override
    @EntityGraph(attributePaths = "beautyItem")
    List<BeautyRecord> findAll(Specification<BeautyRecord> spec, Sort sort);
}
