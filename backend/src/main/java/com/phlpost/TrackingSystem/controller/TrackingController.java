package com.phlpost.TrackingSystem.controller;

import com.phlpost.TrackingSystem.dto.response.TrackingResultDto;
import com.phlpost.TrackingSystem.service.DocumentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final DocumentService documentService;

    public TrackingController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/{referenceNo}")
    public TrackingResultDto track(@PathVariable String referenceNo) {
        return documentService.getTrackingResult(referenceNo);
    }
}
