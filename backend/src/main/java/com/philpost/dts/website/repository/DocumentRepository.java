package com.philpost.dts.website.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.philpost.dts.website.entity.Document;
import com.philpost.dts.website.entity.DocumentStatus;
import com.philpost.dts.website.entity.StepStatus;

import jakarta.persistence.LockModeType;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query(value = """
        select d from Document d
          join fetch d.documentType
          join fetch d.originDepartment
          join fetch d.createdBy
        where d.isArchived = false
          and (:seeAll = true
              or d.originDepartment.departmentId = :departmentId
              or exists (select 1 from RouteStep s
                          where s.route.document = d
                            and s.department.departmentId = :departmentId
                            and s.status <> :waiting))
          and (:status is null or d.status = :status)
          and (:q is null or lower(d.title) like lower(concat('%', :q, '%'))
                          or lower(d.referenceNo) like lower(concat('%', :q, '%')))
        order by d.updatedAt desc
        """,
        countQuery = """
        select count(d) from Document d
        where d.isArchived = false
          and (:seeAll = true
              or d.originDepartment.departmentId = :departmentId
              or exists (select 1 from RouteStep s
                          where s.route.document = d
                            and s.department.departmentId = :departmentId
                            and s.status <> :waiting))
          and (:status is null or d.status = :status)
          and (:q is null or lower(d.title) like lower(concat('%', :q, '%'))
                          or lower(d.referenceNo) like lower(concat('%', :q, '%')))
        """)
    Page<Document> search(@Param("seeAll") boolean seeAll,
                          @Param("departmentId") Long departmentId,
                          @Param("status") DocumentStatus status,
                          @Param("q") String q,
                          @Param("waiting") StepStatus waiting,
                          Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Document d where d.documentId = :id")
    Optional<Document> findByIdForUpdate(@Param("id") Long id);
}