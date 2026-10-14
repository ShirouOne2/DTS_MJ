package com.phlpost.TrackingSystem.controller;

import com.phlpost.TrackingSystem.dto.response.TransmittalDto;
import com.phlpost.TrackingSystem.service.TransmittalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transmittals")
public class TransmittalController {

    private final TransmittalService transmittalService;

    public TransmittalController(TransmittalService transmittalService) {
        this.transmittalService = transmittalService;
    }

    @GetMapping
    public List<TransmittalDto> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return transmittalService.search(fromDate, toDate);
    }

    @GetMapping("/{id}")
    public TransmittalDto getById(@PathVariable UUID id) {
        return transmittalService.getById(id);
    }
}
