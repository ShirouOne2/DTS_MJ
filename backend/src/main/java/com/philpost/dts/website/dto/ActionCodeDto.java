package com.philpost.dts.website.dto;

public record ActionCodeDto(String code, String label, boolean requiresSignature, boolean advisory,
                            String fileRule, String fileKind, boolean instructionRequired) {}