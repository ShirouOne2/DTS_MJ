package com.philpost.dts.website.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.philpost.dts.website.dto.DocumentDetailDto;
import com.philpost.dts.website.dto.StepNoteRequest;
import com.philpost.dts.website.dto.SubmitRouteRequest;
import com.philpost.dts.website.service.RoutingService;

@RestController
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @PostMapping("/documents/{id}/route")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentDetailDto submit(
            @PathVariable Long id,
            @Valid @RequestBody SubmitRouteRequest request) {
        return routingService.submit(id, request);
    }
    @PostMapping("/documents/{id}/steps/{stepId}/receive")
    public DocumentDetailDto receive(@PathVariable Long id, @PathVariable Long stepId) {
        return routingService.receive(id, stepId);
    }

    @PostMapping(value = "/documents/{id}/steps/{stepId}/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentDetailDto complete(@PathVariable Long id, @PathVariable Long stepId,
                                    @RequestParam(required = false) String remarks,
                                    @RequestParam(value = "file", required = false) MultipartFile file) {
        return routingService.complete(id, stepId, remarks, file);
    }

    @PostMapping("/documents/{id}/steps/{stepId}/reject")
    public DocumentDetailDto reject(@PathVariable Long id, @PathVariable Long stepId,
                                    @Valid @RequestBody StepNoteRequest request) {
        return routingService.reject(id, stepId, request.remarks());
    }

    @PostMapping("/documents/{id}/steps/{stepId}/acknowledge")
    public DocumentDetailDto acknowledge(@PathVariable Long id, @PathVariable Long stepId,
                                        @Valid @RequestBody StepNoteRequest request) {
        return routingService.acknowledge(id, stepId, request.remarks());
    }
}