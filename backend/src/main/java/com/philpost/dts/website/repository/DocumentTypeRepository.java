package com.philpost.dts.website.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.philpost.dts.website.entity.DocumentType;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
    List<DocumentType> findByIsActiveTrueOrderByNameAsc();
}