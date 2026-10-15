package com.philpost.dts.website.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.philpost.dts.website.entity.Document;
import com.philpost.dts.website.entity.DocumentStatus;
import com.philpost.dts.website.entity.Role;
import com.philpost.dts.website.entity.RouteStep;
import com.philpost.dts.website.entity.RouteType;
import com.philpost.dts.website.entity.StepStatus;
import com.philpost.dts.website.entity.User;
import com.philpost.dts.website.exception.BusinessRuleException;
import com.philpost.dts.website.exception.NotFoundException;
import com.philpost.dts.website.repository.DocumentRepository;
import com.philpost.dts.website.repository.RouteStepRepository;

@Service
public class DocumentAccessService {

    private final DocumentRepository documentRepository;
    private final RouteStepRepository routeStepRepository;
    

    public DocumentAccessService(DocumentRepository documentRepository, RouteStepRepository routeStepRepository) {
        this.documentRepository = documentRepository;
        this.routeStepRepository = routeStepRepository;
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
        Long departmentId = user.getDepartment().getDepartmentId();
        boolean visible = seesAll(user)
                || doc.getOriginDepartment().getDepartmentId().equals(departmentId)
                || routeStepRepository.hasReached(doc.getDocumentId(), departmentId);
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
    public List<String> stepActions(RouteStep step, User user) {
        List<String> actions = new ArrayList<>();
        if (step.getStatus() != StepStatus.ACTIVE) return actions;
        if (!step.getDepartment().getDepartmentId().equals(user.getDepartment().getDepartmentId())) return actions;

        if (step.getRoute().getRouteType() == RouteType.ADVISORY) {
            actions.add("ACKNOWLEDGE");
        } else if (step.getReceivedAt() == null) {
            actions.add("RECEIVE");
        } else if (!step.getActionCode().isRequiresSignature() || user.getRole() == Role.APPROVER) {
            actions.add("COMPLETE");
            actions.add("REJECT");
        }
        return actions;
    }
}