package com.philpost.dts.website.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.philpost.dts.website.entity.Department;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

// service/ReferenceNumberService.java
// HR-2026-00001, one running counter per department per year.
// The single INSERT ... ON DUPLICATE KEY UPDATE is atomic in MySQL, so two people
// creating documents at the same moment can never receive the same number.
@Service
public class ReferenceNumberService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(propagation = Propagation.MANDATORY)
    public String next(Department department) {
        int year = LocalDate.now().getYear();
        em.createNativeQuery("""
                INSERT INTO reference_counters (department_id, ref_year, last_number)
                VALUES (:d, :y, LAST_INSERT_ID(1))
                ON DUPLICATE KEY UPDATE last_number = LAST_INSERT_ID(last_number + 1)
                """)
                .setParameter("d", department.getDepartmentId())
                .setParameter("y", year)
                .executeUpdate();
        Number n = (Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult();
        return "%s-%d-%05d".formatted(department.getCode(), year, n.longValue());
    }
}