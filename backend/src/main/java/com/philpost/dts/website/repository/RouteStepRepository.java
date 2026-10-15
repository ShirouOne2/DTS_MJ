package com.philpost.dts.website.repository;

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
          and s.status <> :excluded
        """)
    boolean existsReached(@Param("documentId") Long documentId,
                          @Param("departmentId") Long departmentId,
                          @Param("excluded") StepStatus excluded);

    // True once the route has reached this department (its step is no longer WAITING).
    default boolean hasReached(Long documentId, Long departmentId) {
        return existsReached(documentId, departmentId, StepStatus.WAITING);
    }
    Optional<RouteStep> findByStepIdAndRoute_Document_DocumentId(Long stepId, Long documentId);
}