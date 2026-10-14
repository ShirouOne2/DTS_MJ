package com.phlpost.TrackingSystem.controller;

import com.phlpost.TrackingSystem.dto.request.AddRemarkRequest;
import com.phlpost.TrackingSystem.dto.request.CreateDocumentRequest;
import com.phlpost.TrackingSystem.dto.request.ForwardRequest;
import com.phlpost.TrackingSystem.dto.response.DocumentDetailDto;
import com.phlpost.TrackingSystem.dto.response.DocumentFileDto;
import com.phlpost.TrackingSystem.dto.response.DocumentSummaryDto;
import com.phlpost.TrackingSystem.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/recent")
    public List<DocumentSummaryDto> getRecent() {
        return documentService.getRecent();
    }

    @GetMapping
    public List<DocumentSummaryDto> getMyDocuments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        return documentService.getMyDocuments(search, status);
    }

    @GetMapping("/archived")
    public List<DocumentSummaryDto> getArchived(@RequestParam(required = false) String search) {
        return documentService.getArchived(search);
    }

    @GetMapping("/{id}")
    public DocumentDetailDto getById(@PathVariable UUID id) {
        return documentService.getById(id);
    }

    @PostMapping
    public DocumentDetailDto create(@Valid @RequestBody CreateDocumentRequest request) {
        return documentService.create(request);
    }

    @PostMapping("/{id}/files")
    public List<DocumentFileDto> uploadFiles(@PathVariable UUID id, @RequestParam("files") List<MultipartFile> files) {
        return documentService.uploadFiles(id, files);
    }

    @PatchMapping("/{id}/archive")
    public DocumentDetailDto archive(@PathVariable UUID id) {
        return documentService.archive(id);
    }

    @PostMapping("/{id}/forward")
    public DocumentDetailDto forward(@PathVariable UUID id, @Valid @RequestBody ForwardRequest request) {
        return documentService.forward(id, request);
    }

    @PostMapping("/{id}/receive")
    public DocumentDetailDto receive(@PathVariable UUID id) {
        return documentService.receive(id);
    }

    // NOTE: not in the original contract — added for the schema's
    // single-recipient Return flow. See ui-refactor-plan.md.
    @PostMapping("/{id}/return")
    public DocumentDetailDto returnDocument(@PathVariable UUID id) {
        return documentService.returnDocument(id);
    }

    @PostMapping("/{id}/remarks")
    public DocumentDetailDto addRemark(@PathVariable UUID id, @Valid @RequestBody AddRemarkRequest request) {
        return documentService.addComment(id, request.message());
    }
}
