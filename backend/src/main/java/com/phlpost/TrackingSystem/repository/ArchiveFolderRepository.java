package com.phlpost.TrackingSystem.repository;

import com.phlpost.TrackingSystem.entity.ArchiveFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArchiveFolderRepository extends JpaRepository<ArchiveFolder, UUID> {

    // Root-level folders (no parent) — the top of the tree for the UI to render
    List<ArchiveFolder> findByParentFolderIsNull();

    List<ArchiveFolder> findByParentFolderId(UUID parentFolderId);
}
