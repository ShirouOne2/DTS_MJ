package com.philpost.dts.website.dto;

import java.time.LocalDateTime;

import com.philpost.dts.website.entity.DocumentVersion;

public record VersionDto(Long id, int versionNo, String kind, String fileName, long sizeBytes,
                         String sha256, String uploadedByName, LocalDateTime uploadedAt) {

    public static VersionDto from(DocumentVersion v) {
        return new VersionDto(v.getVersionId(), v.getVersionNo(), v.getKind().name(), v.getFileName(),
                v.getSizeBytes(), v.getSha256(),
                v.getUploadedBy().getFirstName() + " " + v.getUploadedBy().getLastName(), v.getUploadedAt());
    }
}