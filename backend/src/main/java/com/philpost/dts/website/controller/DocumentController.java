package com.philpost.dts.website.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.philpost.dts.website.dto.DocumentDetailDto;
import com.philpost.dts.website.dto.DocumentRequest;
import com.philpost.dts.website.dto.DocumentSummaryDto;
import com.philpost.dts.website.dto.DocumentTypeDto;
import com.philpost.dts.website.dto.PageResponse;
import com.philpost.dts.website.entity.DocumentStatus;
import com.philpost.dts.website.service.DocumentService;

import jakarta.validation.Valid;

@RestController
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/document-types")
    public List<DocumentTypeDto> types() {
        return documentService.activeTypes();
    }

    @GetMapping("/documents")
    public PageResponse<DocumentSummaryDto> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return documentService.list(search, status, page, size);
    }

    @GetMapping("/documents/{id}")
    public DocumentDetailDto get(@PathVariable Long id) {
        return documentService.get(id);
    }

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentDetailDto create(@Valid @RequestBody DocumentRequest request) {
        return documentService.create(request);
    }

    @PutMapping("/documents/{id}")
    public DocumentDetailDto update(@PathVariable Long id, @Valid @RequestBody DocumentRequest request) {
        return documentService.update(id, request);
    }

    @PostMapping("/documents/{id}/cancel")
    public DocumentDetailDto cancel(@PathVariable Long id) {
        return documentService.cancel(id);
    }
}