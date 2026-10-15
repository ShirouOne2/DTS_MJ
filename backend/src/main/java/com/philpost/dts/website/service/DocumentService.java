package com.philpost.dts.website.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.philpost.dts.website.dto.DocumentDetailDto;
import com.philpost.dts.website.dto.DocumentRequest;
import com.philpost.dts.website.dto.DocumentSummaryDto;
import com.philpost.dts.website.dto.DocumentTypeDto;
import com.philpost.dts.website.dto.PageResponse;
import com.philpost.dts.website.dto.VersionDto;
import com.philpost.dts.website.entity.AuditAction;
import com.philpost.dts.website.entity.Document;
import com.philpost.dts.website.entity.DocumentStatus;
import com.philpost.dts.website.entity.DocumentType;
import com.philpost.dts.website.entity.User;

import com.philpost.dts.website.repository.DocumentRepository;
import com.philpost.dts.website.repository.DocumentTypeRepository;
import com.philpost.dts.website.repository.DocumentVersionRepository;
import com.philpost.dts.website.exception.BusinessRuleException;
import com.philpost.dts.website.exception.NotFoundException;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentVersionRepository versionRepository;
    private final ReferenceNumberService referenceNumbers;
    private final CurrentUserService currentUserService;
    private final DocumentAccessService access;
    private final AuditService auditService;

    public DocumentService(DocumentRepository documentRepository, DocumentTypeRepository documentTypeRepository,
                           DocumentVersionRepository versionRepository, ReferenceNumberService referenceNumbers,
                           CurrentUserService currentUserService, DocumentAccessService access,
                           AuditService auditService) {
        this.documentRepository = documentRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.versionRepository = versionRepository;
        this.referenceNumbers = referenceNumbers;
        this.currentUserService = currentUserService;
        this.access = access;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<DocumentTypeDto> activeTypes() {
        return documentTypeRepository.findByIsActiveTrueOrderByNameAsc().stream()
                .map(t -> new DocumentTypeDto(t.getDocumentTypeId(), t.getName())).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<DocumentSummaryDto> list(String search, DocumentStatus status, int page, int size) {
        User user = currentUserService.get();
        Page<Document> result = documentRepository.search(
                access.seesAll(user), user.getDepartment().getDepartmentId(), status,
                search == null || search.isBlank() ? null : search.trim(),
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100)));
        return new PageResponse<>(result.getContent().stream().map(this::toSummary).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    // Not readOnly: viewing writes an audit entry.
    @Transactional
    public DocumentDetailDto get(Long id) {
        User user = currentUserService.get();
        Document doc = access.requireVisible(id, user);
        auditService.log(AuditAction.DOCUMENT_VIEWED, "DOCUMENT", id, doc.getReferenceNo());
        return toDetail(doc, user);
    }

    @Transactional
    public DocumentDetailDto create(DocumentRequest request) {
        User user = currentUserService.get();
        Document doc = documentRepository.save(Document.builder()
                .referenceNo(referenceNumbers.next(user.getDepartment()))
                .title(request.title().trim())
                .documentType(activeType(request.documentTypeId()))
                .remarks(blankToNull(request.remarks()))
                .originDepartment(user.getDepartment())
                .createdBy(user)
                .status(DocumentStatus.DRAFT)
                .build());
        auditService.log(AuditAction.DOCUMENT_CREATED, "DOCUMENT", doc.getDocumentId(), doc.getReferenceNo());
        return toDetail(doc, user);
    }

    @Transactional
    public DocumentDetailDto update(Long id, DocumentRequest request) {
        User user = currentUserService.get();
        Document doc = access.requireVisible(id, user);
        access.requireCanEdit(doc, user);
        doc.setTitle(request.title().trim());
        doc.setDocumentType(activeType(request.documentTypeId()));
        doc.setRemarks(blankToNull(request.remarks()));
        auditService.log(AuditAction.DOCUMENT_UPDATED, "DOCUMENT", id, doc.getReferenceNo());
        return toDetail(doc, user);
    }

    @Transactional
    public DocumentDetailDto cancel(Long id) {
        User user = currentUserService.get();
        Document doc = access.requireVisible(id, user);
        access.requireCreator(doc, user);
        if (doc.getStatus() != DocumentStatus.DRAFT) {
            throw new BusinessRuleException("Only a draft can be cancelled.");
        }
        doc.setStatus(DocumentStatus.CANCELLED);
        auditService.log(AuditAction.DOCUMENT_CANCELLED, "DOCUMENT", id, doc.getReferenceNo());
        return toDetail(doc, user);
    }

    private List<String> allowedActions(Document doc, User user) {
        List<String> actions = new ArrayList<>();
        if (access.canEdit(doc, user)) {
            actions.add("EDIT");
            actions.add("UPLOAD");
        }
        if (access.isCreator(doc, user) && doc.getStatus() == DocumentStatus.DRAFT) actions.add("CANCEL");
        return actions;
    }

    private DocumentType activeType(Long id) {
        DocumentType type = documentTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document type not found: " + id));
        if (!Boolean.TRUE.equals(type.getIsActive())) throw new BusinessRuleException("That document type is inactive.");
        return type;
    }

    private String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private DocumentSummaryDto toSummary(Document d) {
        return new DocumentSummaryDto(d.getDocumentId(), d.getReferenceNo(), d.getTitle(),
                d.getDocumentType().getName(), d.getStatus().name(),
                d.getCreatedBy().getFirstName() + " " + d.getCreatedBy().getLastName(),
                d.getOriginDepartment().getName(), d.getCreatedAt(), d.getUpdatedAt());
    }

    private DocumentDetailDto toDetail(Document d, User user) {
        List<VersionDto> versions = versionRepository.findByDocumentIdWithUploader(d.getDocumentId())
                .stream().map(VersionDto::from).toList();
        return new DocumentDetailDto(d.getDocumentId(), d.getReferenceNo(), d.getTitle(),
                d.getDocumentType().getDocumentTypeId(), d.getDocumentType().getName(), d.getRemarks(),
                d.getStatus().name(), d.getOriginDepartment().getDepartmentId(), d.getOriginDepartment().getName(),
                d.getCreatedBy().getFirstName() + " " + d.getCreatedBy().getLastName(),
                d.getCreatedBy().getUsername(), d.getCreatedAt(), d.getUpdatedAt(),
                allowedActions(d, user), versions);
    }
}