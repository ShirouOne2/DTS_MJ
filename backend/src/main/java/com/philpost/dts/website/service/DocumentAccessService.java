package com.philpost.dts.website.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.philpost.dts.website.entity.Document;
import com.philpost.dts.website.entity.DocumentStatus;
import com.philpost.dts.website.entity.Role;
import com.philpost.dts.website.entity.User;
import com.philpost.dts.website.exception.BusinessRuleException;
import com.philpost.dts.website.exception.NotFoundException;
import com.philpost.dts.website.repository.DocumentRepository;

@Service
public class DocumentAccessService {

    private final DocumentRepository documentRepository;

    public DocumentAccessService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public boolean seesAll(User user) {
        return user.getRole() == Role.ADMIN || user.getRole() == Role.RECORDS;
    }

    public Document requireVisible(Long id, User user) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
        return requireVisible(doc, user);
    }

    // Someone who may not see a document gets a 404, so its existence isn't revealed.
    public Document requireVisible(Document doc, User user) {
        boolean visible = seesAll(user) || doc.getOriginDepartment().getDepartmentId()
                .equals(user.getDepartment().getDepartmentId());
        if (!visible) throw new NotFoundException("Document not found: " + doc.getDocumentId());
        return doc;
    }

    public boolean isCreator(Document doc, User user) {
        return doc.getCreatedBy().getUserId().equals(user.getUserId());
    }

    public boolean isEditable(Document doc) {
        return doc.getStatus() == DocumentStatus.DRAFT || doc.getStatus() == DocumentStatus.FOR_REVISION;
    }

    public boolean canEdit(Document doc, User user) {
        return isCreator(doc, user) && isEditable(doc);
    }

    public void requireCreator(Document doc, User user) {
        if (!isCreator(doc, user)) {
            throw new AccessDeniedException("Only the person who created this document can do that.");
        }
    }

    public void requireCanEdit(Document doc, User user) {
        requireCreator(doc, user);
        if (!isEditable(doc)) throw new BusinessRuleException("This document can no longer be edited.");
    }
}