package com.philpost.dts.website.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philpost.dts.website.dto.ActionCodeDto;
import com.philpost.dts.website.repository.ActionCodeRepository;

@RestController
public class LookupController {

    private final ActionCodeRepository actionCodeRepository;

    public LookupController(ActionCodeRepository actionCodeRepository) {
        this.actionCodeRepository = actionCodeRepository;
    }

    @GetMapping("/action-codes")
    public List<ActionCodeDto> actionCodes() {
        return actionCodeRepository.findAll(Sort.by("code")).stream()
                .map(c -> new ActionCodeDto(c.getCode(), c.getLabel(), c.isRequiresSignature(), c.isAdvisory(),
                        c.getFileRule().name(), c.getFileKind() != null ? c.getFileKind().name() : null,
                        c.isInstructionRequired()))
                .toList();
    }
}