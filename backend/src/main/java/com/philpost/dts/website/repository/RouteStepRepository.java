package com.philpost.dts.website.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.philpost.dts.website.entity.RouteStep;
import com.philpost.dts.website.entity.StepStatus;

public interface RouteStepRepository extends JpaRepository<RouteStep, Long> {

    @Query("""
        select count(s) > 0 from RouteStep s
        where s.route.document.documentId = :documentId
        and s.department.departmentId = :departmentId
        and s.activatedAt is not null
        """)
    boolean hasReached(@Param("documentId") Long documentId, @Param("departmentId") Long departmentId);
    Optional<RouteStep> findByStepIdAndRoute_Document_DocumentId(Long stepId, Long documentId);

    @Query("""
        select s from RouteStep s
        join fetch s.route r
        join fetch r.document d
        join fetch d.documentType
        join fetch d.originDepartment
        join fetch s.department
        join fetch s.actionCode
        where s.department.departmentId = :departmentId
        and s.status = :status
        order by case when s.dueDate is null then 1 else 0 end, s.dueDate asc, s.activatedAt asc
        """)
    List<RouteStep> findInbox(@Param("departmentId") Long departmentId, @Param("status") StepStatus status);

    @Query("select count(s) from RouteStep s where s.department.departmentId = :departmentId and s.status = :status")
    long countForDepartment(@Param("departmentId") Long departmentId, @Param("status") StepStatus status);

    @Query("""
        select count(s) from RouteStep s
        where s.department.departmentId = :departmentId and s.status = :status and s.dueDate < :today
        """)
    long countOverdue(@Param("departmentId") Long departmentId, @Param("status") StepStatus status,
                    @Param("today") LocalDate today);
}