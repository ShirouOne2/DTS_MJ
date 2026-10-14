package com.philpost.dts.website.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.philpost.dts.website.entity.AuditAction;
import com.philpost.dts.website.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
        select a from AuditLog a
        where (:action is null or a.action = :action)
          and (:username is null or lower(a.actorUsername) like lower(concat('%', :username, '%')))
          and (:from is null or a.createdAt >= :from)
          and (:to is null or a.createdAt < :to)
        order by a.createdAt desc
        """)
    Page<AuditLog> search(
            @Param("action") AuditAction action,
            @Param("username") String username,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
}