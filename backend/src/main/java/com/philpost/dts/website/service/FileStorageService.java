package com.philpost.dts.website.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    public record StoredFile(String relativePath, String sha256, long size) {}

    private final Path root;

    public FileStorageService(@Value("${app.file-storage-root:./file-storage}") String rootPath) {
        this.root = Path.of(rootPath).toAbsolutePath().normalize();
    }

    /** Streams the upload to disk and hashes it in the same pass. */
    public StoredFile store(MultipartFile file, String extension) {
        LocalDate today = LocalDate.now();
        String relative = "documents/%d/%02d/%02d/%s.%s".formatted(
                today.getYear(), today.getMonthValue(), today.getDayOfMonth(), UUID.randomUUID(), extension);
        Path target = resolve(relative);
        Path temp = target.resolveSibling(target.getFileName() + ".part");
        try {
            Files.createDirectories(target.getParent());
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            long size;
            try (InputStream in = new DigestInputStream(file.getInputStream(), digest)) {
                size = Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE);
            return new StoredFile(relative, HexFormat.of().formatHex(digest.digest()), size);
        } catch (IOException | NoSuchAlgorithmException e) {
            try { Files.deleteIfExists(temp); } catch (IOException ignored) { }
            throw new IllegalStateException("Failed to store file", e);
        }
    }

    public Resource load(String relativePath) {
        return new FileSystemResource(resolve(relativePath));
    }

    public void deleteQuietly(String relativePath) {
        try {
            Files.deleteIfExists(resolve(relativePath));
        } catch (IOException | RuntimeException ignored) { }
    }

    // Refuses any path that would escape the storage root.
    private Path resolve(String relativePath) {
        Path path = root.resolve(relativePath).normalize();
        if (!path.startsWith(root)) throw new IllegalArgumentException("Invalid storage path");
        return path;
    }
}