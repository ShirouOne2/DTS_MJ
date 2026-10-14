package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByDocumentIdOrderByCreatedAtAsc(UUID documentId);
}
