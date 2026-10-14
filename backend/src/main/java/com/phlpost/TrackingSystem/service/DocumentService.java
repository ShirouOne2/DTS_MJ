package com.phlpost.TrackingSystem.service;

import com.phlpost.TrackingSystem.dto.request.CreateDocumentRequest;
import com.phlpost.TrackingSystem.dto.request.ForwardRequest;
import com.phlpost.TrackingSystem.dto.response.*;
import com.phlpost.TrackingSystem.entity.*;
import com.phlpost.TrackingSystem.exception.BusinessRuleException;
import com.phlpost.TrackingSystem.exception.NotFoundException;
import com.phlpost.TrackingSystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * The routing/status engine — Section 4 of implementation-plan.md, the
 * highest-risk piece. Every method that mutates a ROUTING_TRANSACTIONS,
 * TRANSACTION_RECIPIENTS, or ACTIONS_NEEDED row ends by calling
 * recomputeStatus(), which is the single place the status-derivation
 * table from the schema doc is implemented. Don't duplicate that logic
 * anywhere else — if a new operation needs a status change, it should
 * change the underlying rows and call recomputeStatus(), not set
 * document.status directly.
 */
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentFileRepository documentFileRepository;
    private final RoutingTransactionRepository routingTransactionRepository;
    private final TransactionRecipientRepository transactionRecipientRepository;
    private final ActionNeededRepository actionNeededRepository;
    private final ActionCodeRepository actionCodeRepository;
    private final TransmittalRepository transmittalRepository;
    private final CommentRepository commentRepository;
    private final DepartmentRepository departmentRepository;
    private final CurrentUserService currentUserService;
    private final FileStorageService fileStorageService;

    public DocumentService(
            DocumentRepository documentRepository,
            DocumentFileRepository documentFileRepository,
            RoutingTransactionRepository routingTransactionRepository,
            TransactionRecipientRepository transactionRecipientRepository,
            ActionNeededRepository actionNeededRepository,
            ActionCodeRepository actionCodeRepository,
            TransmittalRepository transmittalRepository,
            CommentRepository commentRepository,
            DepartmentRepository departmentRepository,
            CurrentUserService currentUserService,
            FileStorageService fileStorageService
    ) {
        this.documentRepository = documentRepository;
        this.documentFileRepository = documentFileRepository;
        this.routingTransactionRepository = routingTransactionRepository;
        this.transactionRecipientRepository = transactionRecipientRepository;
        this.actionNeededRepository = actionNeededRepository;
        this.actionCodeRepository = actionCodeRepository;
        this.transmittalRepository = transmittalRepository;
        this.commentRepository = commentRepository;
        this.departmentRepository = departmentRepository;
        this.currentUserService = currentUserService;
        this.fileStorageService = fileStorageService;
    }

    // =========================================================
    // Create
    // =========================================================

    @Transactional
    public DocumentDetailDto create(CreateDocumentRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Document document = Document.builder()
                .referenceNo(generateReferenceNo())
                .subject(request.subject())
                .remarks(request.remarks())
                .documentType(request.documentType())
                .originDepartment(currentUser.getDepartment())
                .createdBy(currentUser)
                .status(Document.Status.INCOMING)
                .build();
        document = documentRepository.save(document);

        RoutingTransaction transaction = openTransaction(
                document, currentUser.getDepartment(), RoutingTransaction.Type.INITIAL_ROUTING,
                request.recipients(), request.actionsNeeded()
        );

        logTransmittal(document, transaction, Transmittal.Action.CREATED, currentUser.getDepartment(), null);

        if (request.departmentComments() != null && !request.departmentComments().isBlank()) {
            commentRepository.save(Comment.builder()
                    .document(document)
                    .user(currentUser)
                    .message(request.departmentComments())
                    .build());
        }

        recomputeStatus(document, transaction);
        return toDetailDto(documentRepository.save(document));
    }

    // =========================================================
    // File upload
    // =========================================================

    @Transactional
    public List<DocumentFileDto> uploadFiles(UUID documentId, List<MultipartFile> files) {
        Document document = getDocumentOrThrow(documentId);
        User currentUser = currentUserService.getCurrentUser();

        List<DocumentFileDto> uploaded = files.stream().map(file -> {
            UUID fileId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            var stored = fileStorageService.store(file, fileId, now);

            DocumentFile documentFile = DocumentFile.builder()
                    .id(fileId)
                    .document(document)
                    .fileName(file.getOriginalFilename())
                    .filePath(stored.relativePath())
                    .fileSize((int) stored.size())
                    .uploadedAt(now)
                    .build();
            documentFile = documentFileRepository.save(documentFile);
            return toFileDto(documentFile);
        }).toList();

        return uploaded;
    }

    // =========================================================
    // Forward
    // =========================================================

    @Transactional
    public DocumentDetailDto forward(UUID documentId, ForwardRequest request) {
        Document document = getDocumentOrThrow(documentId);
        User currentUser = currentUserService.getCurrentUser();

        RoutingTransaction latest = getLatestTransactionOrThrow(document);
        // A forward following a Return is a resubmission, not a fresh forward —
        // logged distinctly in the immutable TRANSMITTALS history.
        boolean isResubmission = latest.getType() == RoutingTransaction.Type.RETURN;

        RoutingTransaction transaction = openTransaction(
                document, currentUser.getDepartment(), RoutingTransaction.Type.FORWARD,
                request.recipients(), request.actionsNeeded()
        );

        logTransmittal(document, transaction,
                isResubmission ? Transmittal.Action.RESUBMITTED : Transmittal.Action.FORWARDED,
                currentUser.getDepartment(), request.remarks());

        recomputeStatus(document, transaction);
        return toDetailDto(documentRepository.save(document));
    }

    // =========================================================
    // Return — only valid on a single-recipient (chain) transaction
    // =========================================================

    @Transactional
    public DocumentDetailDto returnDocument(UUID documentId) {
        Document document = getDocumentOrThrow(documentId);
        User currentUser = currentUserService.getCurrentUser();

        RoutingTransaction latest = getLatestTransactionOrThrow(document);
        if (latest.getRecipientCount() != 1) {
            throw new BusinessRuleException(
                    "Return is only valid on a single-recipient transaction — this document was routed to " +
                            latest.getRecipientCount() + " departments as a one-way broadcast.");
        }

        latest.setStatus(RoutingTransaction.Status.CLOSED);
        routingTransactionRepository.save(latest);

        RoutingTransaction returnTransaction = RoutingTransaction.builder()
                .document(document)
                .fromDepartment(currentUser.getDepartment())
                .type(RoutingTransaction.Type.RETURN)
                .recipientCount(1)
                .status(RoutingTransaction.Status.OPEN)
                .build();
        returnTransaction = routingTransactionRepository.save(returnTransaction);

        // Return goes back to whoever originated this transaction chain.
        TransactionRecipient recipient = TransactionRecipient.builder()
                .transaction(returnTransaction)
                .department(document.getOriginDepartment())
                .status(TransactionRecipient.Status.PENDING)
                .build();
        transactionRecipientRepository.save(recipient);

        logTransmittal(document, returnTransaction, Transmittal.Action.RETURNED, currentUser.getDepartment(), null);

        recomputeStatus(document, returnTransaction);
        return toDetailDto(documentRepository.save(document));
    }

    // =========================================================
    // Receive — acting department marks its recipient row received
    // =========================================================

    @Transactional
    public DocumentDetailDto receive(UUID documentId) {
        Document document = getDocumentOrThrow(documentId);
        User currentUser = currentUserService.getCurrentUser();

        RoutingTransaction latest = getLatestTransactionOrThrow(document);
        TransactionRecipient recipient = latest.getRecipients().stream()
                .filter(r -> r.getDepartment().getId().equals(currentUser.getDepartment().getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException(
                        "Your department isn't a recipient on this document's current transaction."));

        LocalDateTime now = LocalDateTime.now();
        if (recipient.getDeliveredAt() == null) recipient.setDeliveredAt(now);
        if (recipient.getViewedAt() == null) recipient.setViewedAt(now);
        recipient.setReceivedAt(now);
        recipient.setStatus(TransactionRecipient.Status.RECEIVED);
        transactionRecipientRepository.save(recipient);

        logTransmittal(document, latest, Transmittal.Action.RECEIVED, currentUser.getDepartment(), null);

        closeTransactionIfComplete(latest);
        recomputeStatus(document, latest);
        return toDetailDto(documentRepository.save(document));
    }

    // =========================================================
    // Archive — explicit terminal action, sets Completed
    // =========================================================

    @Transactional
    public DocumentDetailDto archive(UUID documentId) {
        Document document = getDocumentOrThrow(documentId);
        User currentUser = currentUserService.getCurrentUser();

        document.setStatus(Document.Status.COMPLETED);
        document.setArchived(true);
        document.setDateReleased(LocalDateTime.now());

        RoutingTransaction latest = routingTransactionRepository
                .findFirstByDocumentIdOrderByCreatedAtDesc(document.getId()).orElse(null);
        logTransmittal(document, latest, Transmittal.Action.COMPLETED, currentUser.getDepartment(), null);

        return toDetailDto(documentRepository.save(document));
    }

    // =========================================================
    // Comments (the ongoing thread — distinct from Document.remarks)
    // =========================================================

    @Transactional
    public DocumentDetailDto addComment(UUID documentId, String message) {
        Document document = getDocumentOrThrow(documentId);
        User currentUser = currentUserService.getCurrentUser();

        commentRepository.save(Comment.builder()
                .document(document)
                .user(currentUser)
                .message(message)
                .build());

        return toDetailDto(document);
    }

    // =========================================================
    // Reads
    // =========================================================

    public DocumentDetailDto getById(UUID id) {
        return toDetailDto(getDocumentOrThrow(id));
    }

    public List<DocumentSummaryDto> getRecent() {
        return documentRepository.findAll().stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .limit(10)
                .map(this::toSummaryDto)
                .toList();
    }

    public List<DocumentSummaryDto> getMyDocuments(String search, String status) {
        return documentRepository.findByStatusNot(Document.Status.COMPLETED).stream()
                .filter(d -> matchesSearch(d, search))
                .filter(d -> status == null || status.isBlank() || "All".equals(status)
                        || d.getStatus().name().equalsIgnoreCase(status.replace(" ", "_")))
                .map(this::toSummaryDto)
                .toList();
    }

    public List<DocumentSummaryDto> getArchived(String search) {
        return documentRepository.findByArchivedTrue().stream()
                .filter(d -> matchesSearch(d, search))
                .map(this::toSummaryDto)
                .toList();
    }

    /** Backs GET /tracking/{referenceNo} — a narrower public-facing lookup than getById(). */
    public TrackingResultDto getTrackingResult(String referenceNo) {
        Document d = documentRepository.findByReferenceNo(referenceNo)
                .orElseThrow(() -> new NotFoundException("No document found for reference number: " + referenceNo));
        return new TrackingResultDto(
                d.getId(), d.getReferenceNo(), d.getSubject(), d.getDocumentType(),
                d.getStatus().name(), d.getDateCreated(),
                transmittalRepository.findByDocumentIdOrderByCreatedAtAsc(d.getId()).stream()
                        .map(this::toTransmittalDto).toList()
        );
    }

    private boolean matchesSearch(Document d, String search) {
        if (search == null || search.isBlank()) return true;
        return d.getSubject().toLowerCase().contains(search.toLowerCase())
                || d.getReferenceNo().toLowerCase().contains(search.toLowerCase());
    }

    // =========================================================
    // Status derivation — THE single source of truth. See the
    // schema doc's status-derivation table for the reasoning behind
    // every branch here.
    // =========================================================

    private void recomputeStatus(Document document, RoutingTransaction latest) {
        if (latest.getStatus() == RoutingTransaction.Status.CLOSED) {
            document.setStatus(Document.Status.RELEASED);
            return;
        }

        boolean allPending = latest.getRecipients().stream()
                .allMatch(r -> r.getStatus() == TransactionRecipient.Status.PENDING);

        if (latest.getRecipientCount() == 1) {
            if (latest.getType() == RoutingTransaction.Type.RETURN) {
                document.setStatus(Document.Status.FOR_REVISE);
            } else if (latest.getType() == RoutingTransaction.Type.INITIAL_ROUTING) {
                document.setStatus(allPending ? Document.Status.INCOMING : Document.Status.ONGOING);
            } else { // FORWARD
                document.setStatus(allPending ? Document.Status.FORWARDED : Document.Status.ONGOING);
            }
        } else {
            // Multi-recipient: one-way broadcast, no For Revise / Forwarded path.
            document.setStatus(allPending ? Document.Status.INCOMING : Document.Status.ONGOING);
        }
    }

    /** A transaction closes when every recipient has received it AND every action is done. */
    private void closeTransactionIfComplete(RoutingTransaction transaction) {
        boolean anyPendingRecipient = transactionRecipientRepository
                .existsByTransactionIdAndStatusNot(transaction.getId(), TransactionRecipient.Status.RECEIVED);
        boolean anyPendingAction = actionNeededRepository
                .existsByTransactionIdAndStatus(transaction.getId(), ActionNeeded.Status.PENDING);

        if (!anyPendingRecipient && !anyPendingAction) {
            transaction.setStatus(RoutingTransaction.Status.CLOSED);
            routingTransactionRepository.save(transaction);
        }
    }

    // =========================================================
    // Shared transaction-opening logic (create + forward both use this)
    // =========================================================

    private RoutingTransaction openTransaction(
            Document document, Department fromDepartment, RoutingTransaction.Type type,
            List<UUID> recipientDepartmentIds, List<String> actionCodes
    ) {
        RoutingTransaction transaction = RoutingTransaction.builder()
                .document(document)
                .fromDepartment(fromDepartment)
                .type(type)
                .recipientCount(recipientDepartmentIds.size())
                .status(RoutingTransaction.Status.OPEN)
                .build();
        transaction = routingTransactionRepository.save(transaction);

        for (UUID deptId : recipientDepartmentIds) {
            Department dept = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new NotFoundException("Department not found: " + deptId));
            transactionRecipientRepository.save(TransactionRecipient.builder()
                    .transaction(transaction)
                    .department(dept)
                    .status(TransactionRecipient.Status.PENDING)
                    .build());
        }

        // Actions apply to the transaction as a whole, not to any single
        // recipient — a transaction can have 1 or many recipients either way.
        if (actionCodes != null) {
            for (String code : actionCodes) {
                ActionCode actionCode = actionCodeRepository.findById(code)
                        .orElseThrow(() -> new NotFoundException("Unknown action code: " + code));
                actionNeededRepository.save(ActionNeeded.builder()
                        .transaction(transaction)
                        .label(actionCode.getLabel())
                        .actionCode(actionCode)
                        .status(ActionNeeded.Status.PENDING)
                        .build());
            }
        }

        return transaction;
    }

    private void logTransmittal(Document document, RoutingTransaction transaction,
                                 Transmittal.Action action, Department fromDepartment, String remarks) {
        transmittalRepository.save(Transmittal.builder()
                .document(document)
                .transaction(transaction)
                .action(action)
                .fromDepartment(fromDepartment)
                .remarks(remarks)
                .build());
    }

    private String generateReferenceNo() {
        String datePart = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return datePart + "-" + UUID.randomUUID();
    }

    private Document getDocumentOrThrow(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
    }

    private RoutingTransaction getLatestTransactionOrThrow(Document document) {
        return routingTransactionRepository.findFirstByDocumentIdOrderByCreatedAtDesc(document.getId())
                .orElseThrow(() -> new NotFoundException("Document has no routing transaction: " + document.getId()));
    }

    // =========================================================
    // Mapping — hand-written per implementation-plan.md's Section 2
    // decision (MapStruct is an option later; this stays transparent
    // for now while the shape is still moving).
    // =========================================================

    private DocumentSummaryDto toSummaryDto(Document d) {
        return new DocumentSummaryDto(
                d.getId(), d.getReferenceNo(), d.getSubject(), d.getDocumentType(),
                d.getStatus().name(), d.isArchived(),
                d.getOriginDepartment().getName(), d.getCreatedBy().getUsername(),
                d.getDateCreated(), d.getUpdatedAt()
        );
    }

    private DocumentDetailDto toDetailDto(Document d) {
        RoutingTransaction latest = routingTransactionRepository
                .findFirstByDocumentIdOrderByCreatedAtDesc(d.getId()).orElse(null);

        return new DocumentDetailDto(
                d.getId(), d.getReferenceNo(), d.getSubject(), d.getRemarks(), d.getDocumentType(),
                d.getStatus().name(), d.isArchived(),
                new DepartmentDto(d.getOriginDepartment().getId(), d.getOriginDepartment().getName(), d.getOriginDepartment().getCode()),
                new UserSummaryDto(d.getCreatedBy().getId(), d.getCreatedBy().getUsername(), d.getCreatedBy().getDepartment().getName()),
                d.getDateCreated(), d.getTimeCreated(), d.getDateReleased(), d.getUpdatedAt(),
                documentFileRepository.findByDocumentId(d.getId()).stream().map(this::toFileDto).toList(),
                latest != null ? toTransactionDto(latest) : null,
                commentRepository.findByDocumentIdOrderByCreatedAtAsc(d.getId()).stream().map(this::toCommentDto).toList(),
                transmittalRepository.findByDocumentIdOrderByCreatedAtAsc(d.getId()).stream().map(this::toTransmittalDto).toList()
        );
    }

    private DocumentFileDto toFileDto(DocumentFile f) {
        return new DocumentFileDto(f.getId(), f.getFileName(), f.getFilePath(), f.getFileSize(), f.getUploadedAt());
    }

    private RoutingTransactionDto toTransactionDto(RoutingTransaction t) {
        return new RoutingTransactionDto(
                t.getId(), t.getType().name().toLowerCase(), t.getStatus().name().toLowerCase(),
                t.getFromDepartment().getName(), t.getRecipientCount(),
                t.getRecipients().stream().map(this::toRecipientDto).toList(),
                t.getActionsNeeded().stream().map(this::toActionDto).toList(),
                t.getCreatedAt()
        );
    }

    private TransactionRecipientDto toRecipientDto(TransactionRecipient r) {
        return new TransactionRecipientDto(
                r.getId(), r.getDepartment().getName(), r.getStatus().name().toLowerCase(),
                r.getDeliveredAt(), r.getViewedAt(), r.getReceivedAt()
        );
    }

    private ActionNeededDto toActionDto(ActionNeeded a) {
        return new ActionNeededDto(
                a.getId(), a.getLabel(), a.getActionCode() != null ? a.getActionCode().getCode() : null,
                a.getAssignedDepartment() != null ? a.getAssignedDepartment().getName() : null,
                a.getDueDate(), a.getStatus().name().toLowerCase()
        );
    }

    private CommentDto toCommentDto(Comment c) {
        return new CommentDto(c.getId(), c.getUser().getUsername(), c.getMessage(), c.getCreatedAt());
    }

    private TransmittalDto toTransmittalDto(Transmittal t) {
        String recipientSummary = null;
        if (t.getTransaction() != null) {
            List<TransactionRecipient> recipients = t.getTransaction().getRecipients();
            if (recipients.size() == 1) {
                recipientSummary = recipients.get(0).getDepartment().getName();
            } else if (recipients.size() > 1) {
                recipientSummary = recipients.size() + " departments";
            }
        }
        return new TransmittalDto(
                t.getId(), t.getDocument().getId(), t.getDocument().getReferenceNo(),
                t.getTransaction() != null ? t.getTransaction().getId() : null,
                formatActionLabel(t.getAction()),
                t.getFromDepartment() != null ? t.getFromDepartment().getName() : null,
                recipientSummary, t.getRemarks(), t.getCreatedAt()
        );
    }

    private String formatActionLabel(Transmittal.Action action) {
        // CREATED -> "Created", ACTION_COMPLETED -> "Action Completed", etc.
        String[] words = action.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            sb.append(w.charAt(0)).append(w.substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
    }
}