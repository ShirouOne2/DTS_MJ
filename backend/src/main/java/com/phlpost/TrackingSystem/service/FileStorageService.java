package com.phlpost.TrackingSystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Implements the documents/{YYYY}/{MM}/{DD}/{file_id}_{file_name} storage
 * convention from the schema doc — a real disk path, independent of
 * ARCHIVE_FOLDERS (which is UI-only). Swap storageRoot to a NAS mount path
 * later; nothing else about this convention needs to change when you do.
 */
@Service
public class FileStorageService {

    private final Path storageRoot;

    public FileStorageService(@Value("${app.file-storage-root:./file-storage}") String storageRootPath) {
        this.storageRoot = Path.of(storageRootPath);
    }

    public record StoredFile(String relativePath, long size) {}

    public StoredFile store(MultipartFile file, UUID fileId, LocalDateTime uploadedAt) {
        String datePrefix = uploadedAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = "documents/%s/%s_%s".formatted(datePrefix, fileId, file.getOriginalFilename());

        Path fullPath = storageRoot.resolve(relativePath);
        try {
            Files.createDirectories(fullPath.getParent());
            file.transferTo(fullPath);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file: " + file.getOriginalFilename(), e);
        }

        return new StoredFile(relativePath, file.getSize());
    }
}
