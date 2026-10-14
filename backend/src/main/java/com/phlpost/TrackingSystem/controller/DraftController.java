package com.phlpost.TrackingSystem.controller;

import com.phlpost.TrackingSystem.dto.request.SaveDraftRequest;
import com.phlpost.TrackingSystem.dto.response.DraftDto;
import com.phlpost.TrackingSystem.dto.response.DraftSummaryDto;
import com.phlpost.TrackingSystem.service.DraftService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/drafts")
public class DraftController {

    private final DraftService draftService;

    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

    @PostMapping
    public DraftDto save(@Valid @RequestBody SaveDraftRequest request) {
        return draftService.save(request);
    }

    @GetMapping
    public List<DraftSummaryDto> list() {
        return draftService.listForCurrentUser();
    }

    @GetMapping("/{id}")
    public DraftDto getById(@PathVariable UUID id) {
        return draftService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        draftService.delete(id);
    }
}
