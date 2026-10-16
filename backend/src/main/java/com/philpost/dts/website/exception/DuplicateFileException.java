package com.philpost.dts.website.exception;

import lombok.Getter;

@Getter
public class DuplicateFileException extends RuntimeException {
    private final String scope;        // SAME_DOCUMENT or OTHER_DOCUMENT
    private final String referenceNo;  // null when the user can't open that document
    private final Integer versionNo;

    public DuplicateFileException(String message, String scope, String referenceNo, Integer versionNo) {
        super(message);
        this.scope = scope;
        this.referenceNo = referenceNo;
        this.versionNo = versionNo;
    }
}