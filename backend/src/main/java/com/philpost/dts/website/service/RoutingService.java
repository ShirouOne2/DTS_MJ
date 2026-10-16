package com.philpost.dts.website.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.philpost.dts.website.dto.DocumentDetailDto;
import com.philpost.dts.website.dto.RouteStepRequest;
import com.philpost.dts.website.dto.SubmitRouteRequest;
import com.philpost.dts.website.entity.ActionCode;
import com.philpost.dts.website.entity.AuditAction;
import com.philpost.dts.website.entity.Department;
import com.philpost.dts.website.entity.Document;
import com.philpost.dts.website.entity.DocumentStatus;
import com.philpost.dts.website.entity.DocumentVersion;
import com.philpost.dts.website.entity.FileRule;
import com.philpost.dts.website.entity.Route;
import com.philpost.dts.website.entity.RouteStatus;
import com.philpost.dts.website.entity.RouteStep;
import com.philpost.dts.website.entity.RouteType;
import com.philpost.dts.website.entity.StepDecision;
import com.philpost.dts.website.entity.StepStatus;
import com.philpost.dts.website.entity.User;
import com.philpost.dts.website.entity.VersionKind;
import com.philpost.dts.website.exception.BusinessRuleException;
import com.philpost.dts.website.exception.NotFoundException;
import com.philpost.dts.website.repository.ActionCodeRepository;
import com.philpost.dts.website.repository.DepartmentRepository;
import com.philpost.dts.website.repository.DocumentRepository;
import com.philpost.dts.website.repository.DocumentVersionRepository;
import com.philpost.dts.website.repository.RouteRepository;
import com.philpost.dts.website.repository.RouteStepRepository;

import jakarta.transaction.Transactional;

@Service
public class RoutingService {

    private static final String ADVISORY_CODE = "FYI/F";

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final RouteRepository routeRepository;
    private final DepartmentRepository departmentRepository;
    private final ActionCodeRepository actionCodeRepository;
    private final CurrentUserService currentUserService;
    private final DocumentAccessService access;
    private final DocumentService documentService;
    private final AuditService auditService;
    private final RouteStepRepository routeStepRepository;
    private final DocumentFileService fileService;

    public RoutingService(DocumentRepository documentRepository, DocumentVersionRepository versionRepository,
                          RouteRepository routeRepository, DepartmentRepository departmentRepository,
                          ActionCodeRepository actionCodeRepository, CurrentUserService currentUserService,
                          DocumentAccessService access, DocumentService documentService,
                          AuditService auditService, RouteStepRepository routeStepRepository, 
                          DocumentFileService fileService) {
        this.documentRepository = documentRepository;
        this.versionRepository = versionRepository;
        this.routeRepository = routeRepository;
        this.departmentRepository = departmentRepository;
        this.actionCodeRepository = actionCodeRepository;
        this.currentUserService = currentUserService;
        this.access = access;
        this.documentService = documentService;
        this.auditService = auditService;
        this.routeStepRepository = routeStepRepository;
        this.fileService = fileService;
    }

    @Transactional
    public DocumentDetailDto submit(Long documentId, SubmitRouteRequest request) {
        User user = currentUserService.get();

        // Row lock: only one routing action on a document at a time.
        Document doc = documentRepository.findByIdForUpdate(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found: " + documentId));
        access.requireVisible(doc, user);
        access.requireCreator(doc, user);
        if (!access.isEditable(doc)) {
            throw new BusinessRuleException("Only a draft, or a document returned for revision, can be submitted.");
        }

        DocumentVersion current = versionRepository.findByDocumentIdWithUploader(documentId)
                .stream().findFirst()   // newest first
                .orElseThrow(() -> new BusinessRuleException("Upload a file before submitting."));
        if (doc.getStatus() == DocumentStatus.FOR_REVISION) {
            Route lastRound = routeRepository.findByDocumentIdWithDetails(documentId).get(0);
            if (lastRound.getVersion().getVersionId().equals(current.getVersionId())) {
                throw new BusinessRuleException("Upload a corrected file before resubmitting.");
            }
        }
        boolean advisory = request.routeType() == RouteType.ADVISORY;
        Long originId = doc.getOriginDepartment().getDepartmentId();
        LocalDateTime now = LocalDateTime.now();

        Route route = Route.builder()
                .document(doc)
                .roundNo(routeRepository.maxRoundNo(documentId) + 1)
                .routeType(request.routeType())
                .status(RouteStatus.ACTIVE)
                .version(current)
                .submittedBy(user)
                .remarks(blankToNull(request.remarks()))
                .startedAt(now)
                .build();

        Set<Long> seen = new HashSet<>();
        int order = 1;
        for (RouteStepRequest sr : request.steps()) {
            Department dept = departmentRepository.findById(sr.departmentId())
                    .orElseThrow(() -> new NotFoundException("Department not found: " + sr.departmentId()));
            if (!Boolean.TRUE.equals(dept.getIsActive())) {
                throw new BusinessRuleException(dept.getName() + " is inactive.");
            }
            if (sr.dueDate() != null && sr.dueDate().isBefore(LocalDate.now())) {
                throw new BusinessRuleException("A due date can't be in the past.");
            }

            ActionCode code;
            if (advisory) {
                if (dept.getDepartmentId().equals(originId)) {
                    throw new BusinessRuleException("An information route can't be sent to your own department.");
                }
                if (!seen.add(dept.getDepartmentId())) {
                    throw new BusinessRuleException(dept.getName() + " is listed more than once.");
                }
                code = actionCodeRepository.findById(ADVISORY_CODE)
                        .orElseThrow(() -> new IllegalStateException("Action code " + ADVISORY_CODE + " is missing."));
            } else {
                if (sr.actionCode() == null || sr.actionCode().isBlank()) {
                    throw new BusinessRuleException("Choose an action for every step.");
                }
                code = actionCodeRepository.findById(sr.actionCode())
                        .orElseThrow(() -> new NotFoundException("Unknown action code: " + sr.actionCode()));
                if (code.isAdvisory()) {
                    throw new BusinessRuleException(
                            "For Information is its own routing type. Pick an action for each step.");
                }

                if (code.isInstructionRequired() && blankToNull(sr.instruction()) == null) {
                    throw new BusinessRuleException("Step %d: %s needs an instruction saying what is expected."
                            .formatted(order, code.getCode()));
                }
            }

            // For Information: everyone is active at once. For Action: only the first step starts.
            boolean startsNow = advisory || order == 1;
            route.getSteps().add(RouteStep.builder()
                    .route(route)
                    .stepOrder(order++)
                    .department(dept)
                    .actionCode(code)
                    .instruction(blankToNull(sr.instruction()))
                    .dueDate(sr.dueDate())
                    .status(startsNow ? StepStatus.ACTIVE : StepStatus.WAITING)
                    .activatedAt(startsNow ? now : null)
                    .build());
        }

        routeRepository.save(route);
        doc.setStatus(DocumentStatus.IN_ROUTING);

        auditService.log(AuditAction.ROUTE_SUBMITTED, "DOCUMENT", documentId,
                "%s round %d (%s) file v%d, %d step%s".formatted(doc.getReferenceNo(), route.getRoundNo(),
                        route.getRouteType(), current.getVersionNo(), route.getSteps().size(),
                        route.getSteps().size() == 1 ? "" : "s"));
        return documentService.toDetail(doc, user);
    }

    private String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    @Transactional
    public DocumentDetailDto receive(Long documentId, Long stepId) {
        User user = currentUserService.get();
        Document doc = lockVisible(documentId, user);
        RouteStep step = loadStep(documentId, stepId);
        requireAction(step, user, "RECEIVE");

        step.setReceivedBy(user);
        step.setReceivedAt(LocalDateTime.now());
        touch(doc);

        auditService.log(AuditAction.STEP_RECEIVED, "DOCUMENT", documentId, describe(doc, step));
        return documentService.toDetail(doc, user);
    }

    @Transactional
    public DocumentDetailDto complete(Long documentId, Long stepId, String remarks, MultipartFile file,
                                    boolean confirmDuplicate) {
        User user = currentUserService.get();
        Document doc = lockVisible(documentId, user);
        RouteStep step = loadStep(documentId, stepId);
        requireAction(step, user, "COMPLETE");

        ActionCode code = step.getActionCode();
        boolean hasFile = file != null && !file.isEmpty();
        VersionKind kind = code.getFileKind() != null ? code.getFileKind() : VersionKind.ATTACHMENT;

        if (hasFile && code.getFileRule() == FileRule.NONE) {
            throw new BusinessRuleException("This step doesn't take a file.");
        }
        if (!hasFile && code.getFileRule() == FileRule.REQUIRED) {
            throw new BusinessRuleException("Attach the " + kind.getLabel().toLowerCase() + " to complete this step.");
        }
        if (hasFile) {
            // Becomes the newest version, so the next office receives it.
            step.setResultVersion(fileService.storeVersion(doc, user, file, kind, confirmDuplicate));
        }

        LocalDateTime now = LocalDateTime.now();
        step.setStatus(StepStatus.DONE);
        step.setDecision(StepDecision.APPROVED);
        step.setDecidedBy(user);
        step.setDecidedAt(now);
        step.setDecisionRemarks(cleanNote(remarks));

        Route route = step.getRoute();
        Optional<RouteStep> next = route.getSteps().stream()
                .filter(s -> s.getStatus() == StepStatus.WAITING).findFirst();
        if (next.isPresent()) {
            next.get().setStatus(StepStatus.ACTIVE);
            next.get().setActivatedAt(now);
        } else {
            finishRoute(route, doc, now);
        }
        touch(doc);

        auditService.log(AuditAction.STEP_COMPLETED, "DOCUMENT", documentId, describe(doc, step));
        return documentService.toDetail(doc, user);
    }

    @Transactional
    public DocumentDetailDto reject(Long documentId, Long stepId, String remarks) {
        User user = currentUserService.get();
        Document doc = lockVisible(documentId, user);
        RouteStep step = loadStep(documentId, stepId);
        requireAction(step, user, "REJECT");

        String reason = cleanNote(remarks);
        if (reason == null) throw new BusinessRuleException("Please give a reason for rejecting.");

        LocalDateTime now = LocalDateTime.now();
        step.setStatus(StepStatus.REJECTED);
        step.setDecision(StepDecision.REJECTED);
        step.setDecidedBy(user);
        step.setDecidedAt(now);
        step.setDecisionRemarks(reason);

        Route route = step.getRoute();
        route.getSteps().stream()
                .filter(s -> s.getStatus() == StepStatus.WAITING)
                .forEach(s -> s.setStatus(StepStatus.SKIPPED));
        route.setStatus(RouteStatus.REJECTED);
        route.setEndedAt(now);
        doc.setStatus(DocumentStatus.FOR_REVISION);
        touch(doc);

        auditService.log(AuditAction.STEP_REJECTED, "DOCUMENT", documentId,
                describe(doc, step) + " - " + reason);
        return documentService.toDetail(doc, user);
    }

    @Transactional
    public DocumentDetailDto acknowledge(Long documentId, Long stepId, String remarks) {
        User user = currentUserService.get();
        Document doc = lockVisible(documentId, user);
        RouteStep step = loadStep(documentId, stepId);
        requireAction(step, user, "ACKNOWLEDGE");

        LocalDateTime now = LocalDateTime.now();
        if (step.getReceivedAt() == null) {
            step.setReceivedBy(user);
            step.setReceivedAt(now);
        }
        step.setStatus(StepStatus.DONE);
        step.setDecision(StepDecision.ACKNOWLEDGED);
        step.setDecidedBy(user);
        step.setDecidedAt(now);
        step.setDecisionRemarks(cleanNote(remarks));

        Route route = step.getRoute();
        if (route.getSteps().stream().allMatch(s -> s.getStatus() == StepStatus.DONE)) {
            finishRoute(route, doc, now);
        }
        touch(doc);

        auditService.log(AuditAction.STEP_ACKNOWLEDGED, "DOCUMENT", documentId, describe(doc, step));
        return documentService.toDetail(doc, user);
    }

    // --- helpers ---

    private Document lockVisible(Long documentId, User user) {
        Document doc = documentRepository.findByIdForUpdate(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found: " + documentId));
        access.requireVisible(doc, user);
        return doc;
    }

    private RouteStep loadStep(Long documentId, Long stepId) {
        return routeStepRepository.findByStepIdAndRoute_Document_DocumentId(stepId, documentId)
                .orElseThrow(() -> new NotFoundException("Step not found: " + stepId));
    }

    private void requireAction(RouteStep step, User user, String action) {
        if (!step.getDepartment().getDepartmentId().equals(user.getDepartment().getDepartmentId())) {
            throw new AccessDeniedException("This step belongs to another department.");
        }
        if (step.getStatus() != StepStatus.ACTIVE) {
            throw new BusinessRuleException("This step is no longer waiting for action.");
        }
        if (access.stepActions(step, user).contains(action)) return;

        boolean decides = action.equals("COMPLETE") || action.equals("REJECT");
        if (decides && step.getRoute().getRouteType() == RouteType.ACTION && step.getReceivedAt() == null) {
            throw new BusinessRuleException("Receive the document first.");
        }
        if (decides && step.getActionCode().isRequiresSignature()) {
            throw new AccessDeniedException("Only an Approver of your department can sign or reject this step.");
        }
        throw new BusinessRuleException("That action isn't available for this step.");
    }

    private void finishRoute(Route route, Document doc, LocalDateTime now) {
        route.setStatus(RouteStatus.COMPLETED);
        route.setEndedAt(now);
        doc.setStatus(DocumentStatus.COMPLETED);
        doc.setCompletedAt(now);
        auditService.log(AuditAction.ROUTE_COMPLETED, "DOCUMENT", doc.getDocumentId(),
                "%s round %d completed".formatted(doc.getReferenceNo(), route.getRoundNo()));
    }

    private void touch(Document doc) {
        doc.setUpdatedAt(LocalDateTime.now());
    }

    private String cleanNote(String s) {
        String note = blankToNull(s);
        if (note != null && note.length() > 1000) throw new BusinessRuleException("The note is too long (max 1000).");
        return note;
    }

    private String describe(Document doc, RouteStep step) {
        return "%s round %d step %d, %s (%s)".formatted(doc.getReferenceNo(), step.getRoute().getRoundNo(),
                step.getStepOrder(), step.getDepartment().getName(), step.getActionCode().getCode());
    }
    @Transactional
    public DocumentDetailDto recall(Long documentId) {
        User user = currentUserService.get();
        Document doc = lockVisible(documentId, user);
        access.requireCreator(doc, user);

        Route round = routeRepository.findByDocumentIdWithDetails(documentId).stream().findFirst().orElse(null);
        if (!access.canRecall(doc, user, round)) {
            throw new BusinessRuleException(
                    "This document can't be recalled. A recall is only possible before any department has received or acted on it.");
        }

        LocalDateTime now = LocalDateTime.now();
        round.getSteps().forEach(s -> s.setStatus(StepStatus.SKIPPED));
        round.setStatus(RouteStatus.RECALLED);
        round.setEndedAt(now);
        doc.setStatus(DocumentStatus.DRAFT);
        touch(doc);

        auditService.log(AuditAction.ROUTE_RECALLED, "DOCUMENT", documentId,
                "%s round %d recalled".formatted(doc.getReferenceNo(), round.getRoundNo()));
        return documentService.toDetail(doc, user);
    }
}