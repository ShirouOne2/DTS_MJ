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

    public record StagedFile(Path temp, String extension, String sha256, long size) {}
    public record StoredFile(String relativePath, boolean reused) {}

    private final Path root;

    public FileStorageService(@Value("${app.file-storage-root:./file-storage}") String rootPath) {
        this.root = Path.of(rootPath).toAbsolutePath().normalize();
    }

    /** Step 1: stream the upload into a temporary file, hashing in the same pass. */
    public StagedFile stage(MultipartFile file, String extension) {
        Path temp = root.resolve("tmp").resolve(UUID.randomUUID() + ".part");
        try {
            Files.createDirectories(temp.getParent());
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            long size;
            try (InputStream in = new DigestInputStream(file.getInputStream(), digest)) {
                size = Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
            }
            return new StagedFile(temp, extension, HexFormat.of().formatHex(digest.digest()), size);
        } catch (IOException | NoSuchAlgorithmException e) {
            try { Files.deleteIfExists(temp); } catch (IOException ignored) { }
            throw new IllegalStateException("Failed to store file", e);
        }
    }

    /**
     * Step 2: keep the staged file. If an identical file is already on disk, reuse it and drop
     * the temp copy. Stored files are never deleted while any version points at them, so sharing
     * is safe. (A future purge feature must count references before deleting.)
     */
    public StoredFile commit(StagedFile staged, String existingRelativePath) {
        if (existingRelativePath != null && Files.exists(resolve(existingRelativePath))) {
            discard(staged);
            return new StoredFile(existingRelativePath, true);
        }
        LocalDate today = LocalDate.now();
        String relative = "documents/%d/%02d/%02d/%s.%s".formatted(
                today.getYear(), today.getMonthValue(), today.getDayOfMonth(),
                UUID.randomUUID(), staged.extension());
        Path target = resolve(relative);
        try {
            Files.createDirectories(target.getParent());
            Files.move(staged.temp(), target, StandardCopyOption.ATOMIC_MOVE);
            return new StoredFile(relative, false);
        } catch (IOException e) {
            discard(staged);
            throw new IllegalStateException("Failed to store file", e);
        }
    }

    /** Safe to call after commit: it only removes the temp file if it still exists. */
    public void discard(StagedFile staged) {
        try { Files.deleteIfExists(staged.temp()); } catch (IOException ignored) { }
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