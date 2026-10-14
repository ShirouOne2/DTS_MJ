package com.philpost.dts.website.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.philpost.dts.website.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);
}