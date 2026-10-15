package com.philpost.dts.website.controller;

import java.nio.charset.StandardCharsets;

import com.philpost.dts.website.service.DocumentFileService;
import com.philpost.dts.website.dto.DocumentDetailDto;
import com.philpost.dts.website.dto.VersionDto;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
public class DocumentFileController {

    private final DocumentFileService fileService;

    public DocumentFileController(DocumentFileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/documents/{id}/versions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentDetailDto upload(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return fileService.upload(id, file);
    }

    @GetMapping("/documents/{id}/versions/{versionId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id, @PathVariable Long versionId) {
        DocumentFileService.DownloadFile f = fileService.download(id, versionId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(f.contentType()))
                .contentLength(f.size())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(f.fileName(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(f.resource());
    }
}