package com.phlpost.TrackingSystem.controller;

import com.phlpost.TrackingSystem.dto.response.ActionCodeDto;
import com.phlpost.TrackingSystem.repository.ActionCodeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/action-codes")
public class ActionCodeController {

    private final ActionCodeRepository actionCodeRepository;

    public ActionCodeController(ActionCodeRepository actionCodeRepository) {
        this.actionCodeRepository = actionCodeRepository;
    }

    @GetMapping
    public List<ActionCodeDto> getAll() {
        return actionCodeRepository.findAll().stream()
                .map(c -> new ActionCodeDto(c.getCode(), c.getLabel(), c.getDefaultText()))
                .toList();
    }
}