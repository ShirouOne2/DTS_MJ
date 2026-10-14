package com.phlpost.TrackingSystem.service;

import com.phlpost.TrackingSystem.dto.request.SaveDraftRequest;
import com.phlpost.TrackingSystem.dto.response.DraftDto;
import com.phlpost.TrackingSystem.dto.response.DraftSummaryDto;
import com.phlpost.TrackingSystem.entity.Draft;
import com.phlpost.TrackingSystem.entity.User;
import com.phlpost.TrackingSystem.exception.NotFoundException;
import com.phlpost.TrackingSystem.repository.DocumentRepository;
import com.phlpost.TrackingSystem.repository.DraftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DraftService {

    private final DraftRepository draftRepository;
    private final DocumentRepository documentRepository;
    private final CurrentUserService currentUserService;

    public DraftService(DraftRepository draftRepository, DocumentRepository documentRepository,
                         CurrentUserService currentUserService) {
        this.draftRepository = draftRepository;
        this.documentRepository = documentRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public DraftDto save(SaveDraftRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Draft draft = request.id() != null
                ? draftRepository.findById(request.id())
                    .orElseThrow(() -> new NotFoundException("Draft not found: " + request.id()))
                : Draft.builder().user(currentUser).build();

        draft.setFormData(request.formData());
        draft.setLastSavedAt(java.time.LocalDateTime.now());

        if (request.documentId() != null) {
            draft.setDocument(documentRepository.findById(request.documentId())
                    .orElseThrow(() -> new NotFoundException("Document not found: " + request.documentId())));
        }

        Draft saved = draftRepository.save(draft);
        return toDto(saved);
    }

    public List<DraftSummaryDto> listForCurrentUser() {
        User currentUser = currentUserService.getCurrentUser();
        return draftRepository.findByUserId(currentUser.getId()).stream()
                .map(this::toSummaryDto)
                .toList();
    }

    public DraftDto getById(UUID id) {
        return draftRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Draft not found: " + id));
    }

    @Transactional
    public void delete(UUID id) {
        if (!draftRepository.existsById(id)) {
            throw new NotFoundException("Draft not found: " + id);
        }
        draftRepository.deleteById(id);
    }

    private DraftDto toDto(Draft draft) {
        return new DraftDto(
                draft.getId(),
                draft.getDocument() != null ? draft.getDocument().getId() : null,
                draft.getFormData(),
                draft.getLastSavedAt()
        );
    }

    private DraftSummaryDto toSummaryDto(Draft draft) {
        Object subject = draft.getFormData() != null ? draft.getFormData().get("subject") : null;
        return new DraftSummaryDto(
                draft.getId(),
                draft.getDocument() != null ? draft.getDocument().getId() : null,
                subject != null ? subject.toString() : null,
                draft.getLastSavedAt()
        );
    }
}
