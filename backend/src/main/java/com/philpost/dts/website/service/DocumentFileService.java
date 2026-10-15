package com.philpost.dts.website.service;

import java.util.Map;
import java.util.TreeSet;
import java.time.LocalDateTime;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.philpost.dts.website.dto.VersionDto;
import com.philpost.dts.website.dto.DocumentDetailDto;
import com.philpost.dts.website.entity.AuditAction;
import com.philpost.dts.website.entity.Document;
import com.philpost.dts.website.entity.DocumentVersion;
import com.philpost.dts.website.entity.User;
import com.philpost.dts.website.entity.VersionKind;
import com.philpost.dts.website.exception.BusinessRuleException;
import com.philpost.dts.website.exception.NotFoundException;
import com.philpost.dts.website.repository.DocumentRepository;
import com.philpost.dts.website.repository.DocumentVersionRepository;
@Service
public class DocumentFileService {

    private static final long MAX_BYTES = 50L * 1024 * 1024;

    // Allow-list: extension -> content type the server will use. The browser's claim is ignored.
    private static final Map<String, String> ALLOWED = Map.ofEntries(
            Map.entry("pdf", "application/pdf"),
            Map.entry("doc", "application/msword"),
            Map.entry("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            Map.entry("xls", "application/vnd.ms-excel"),
            Map.entry("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            Map.entry("ppt", "application/vnd.ms-powerpoint"),
            Map.entry("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
            Map.entry("png", "image/png"),
            Map.entry("jpg", "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("txt", "text/plain"),
            Map.entry("csv", "text/csv"));

    public record DownloadFile(Resource resource, String fileName, String contentType, long size) {}

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final DocumentAccessService access;
    private final FileStorageService storage;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;
    private final DocumentService documentService;

    public DocumentFileService(DocumentRepository documentRepository, DocumentVersionRepository versionRepository,
                               DocumentAccessService access, FileStorageService storage,
                               CurrentUserService currentUserService, AuditService auditService, DocumentService documentService) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
        this.access = access;
        this.storage = storage;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
        this.documentService = documentService;
    }

    @Transactional
    public DocumentDetailDto upload(Long documentId, MultipartFile file) {
        User user = currentUserService.get();

        // Row lock: two uploads to the same document take turns, so version numbers never collide.
        Document doc = documentRepository.findByIdForUpdate(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found: " + documentId));
        access.requireVisible(doc, user);
        access.requireCanEdit(doc, user);

        storeVersion(doc, user, file, null);
        return documentService.toDetail(doc, user);
    }

    /**
     * Validates, stores and records a file as the document's next version.
     * The caller must already have checked permissions and hold the document row lock.
     * kind = null means ORIGINAL for version 1 and REVISION after that.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public DocumentVersion storeVersion(Document doc, User user, MultipartFile file, VersionKind kind) {
        if (file == null || file.isEmpty()) throw new BusinessRuleException("Please choose a file.");
        if (file.getSize() > MAX_BYTES) throw new BusinessRuleException("File exceeds the 50 MB limit.");

        String fileName = cleanFileName(file.getOriginalFilename());
        String extension = extensionOf(fileName);
        String contentType = ALLOWED.get(extension);
        if (contentType == null) {
            throw new BusinessRuleException("That file type is not allowed. Allowed: "
                    + String.join(", ", new TreeSet<>(ALLOWED.keySet())) + ".");
        }

        FileStorageService.StoredFile stored = storage.store(file, extension);

        // If the database transaction rolls back, don't leave the file behind.
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) storage.deleteQuietly(stored.relativePath());
            }
        });

        int versionNo = versionRepository.maxVersionNo(doc.getDocumentId()) + 1;
        VersionKind actualKind = kind != null ? kind
                : (versionNo == 1 ? VersionKind.ORIGINAL : VersionKind.REVISION);

        DocumentVersion version = versionRepository.save(DocumentVersion.builder()
                .document(doc)
                .versionNo(versionNo)
                .kind(actualKind)
                .fileName(fileName)
                .contentType(contentType)
                .storagePath(stored.relativePath())
                .sha256(stored.sha256())
                .sizeBytes(stored.size())
                .uploadedBy(user)
                .build());

        doc.setUpdatedAt(LocalDateTime.now());
        auditService.log(AuditAction.VERSION_UPLOADED, "DOCUMENT", doc.getDocumentId(),
                "%s v%d (%s) %s sha256=%s".formatted(doc.getReferenceNo(), versionNo, actualKind, fileName, stored.sha256()));
        return version;
    }

    @Transactional
    public DownloadFile download(Long documentId, Long versionId) {
        User user = currentUserService.get();
        Document doc = access.requireVisible(documentId, user);
        DocumentVersion version = versionRepository.findByVersionIdAndDocument_DocumentId(versionId, documentId)
                .orElseThrow(() -> new NotFoundException("Version not found: " + versionId));

        Resource resource = storage.load(version.getStoragePath());
        if (!resource.exists()) {
            throw new NotFoundException("The stored file is missing. Please contact an administrator.");
        }
        auditService.log(AuditAction.DOCUMENT_DOWNLOADED, "DOCUMENT", documentId,
                "%s v%d %s".formatted(doc.getReferenceNo(), version.getVersionNo(), version.getFileName()));
        return new DownloadFile(resource, version.getFileName(), version.getContentType(), version.getSizeBytes());
    }

    // Keep only the file name (no folders), drop control characters, cap the length.
    private String cleanFileName(String original) {
        String name = original == null ? "" : original;
        name = name.substring(Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\')) + 1)
                .replaceAll("\\p{Cntrl}", "").trim();
        if (name.length() > 255) {
            String ext = extensionOf(name);
            name = name.substring(0, 250 - ext.length()) + "." + ext;
        }
        return name.isBlank() ? "file" : name;
    }

    private String extensionOf(String name) {
        int dot = name.lastIndexOf('.');
        return dot < 0 || dot == name.length() - 1 ? "" : name.substring(dot + 1).toLowerCase();
    }
}