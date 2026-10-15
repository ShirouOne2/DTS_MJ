package com.philpost.dts.website.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.philpost.dts.website.entity.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {

    @Query("select coalesce(max(r.roundNo), 0) from Route r where r.document.documentId = :documentId")
    int maxRoundNo(@Param("documentId") Long documentId);

    @Query("""
        select r from Route r
          join fetch r.submittedBy
          join fetch r.version
        where r.document.documentId = :documentId
        order by r.roundNo desc
        """)
    List<Route> findByDocumentIdWithDetails(@Param("documentId") Long documentId);
}