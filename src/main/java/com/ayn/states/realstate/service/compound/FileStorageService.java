package com.ayn.states.realstate.service.compound;

import com.ayn.states.realstate.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.regex.Pattern;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    // Pre-compiled pattern for better performance
    private static final Pattern INVALID_CHARS_PATTERN = Pattern.compile("[^a-zA-Z0-9._-]");

    // Use SecureRandom for better uniqueness (cached instance)
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Buffer size for file operations
    private static final int BUFFER_SIZE = 8192;

    private final Path fileStorageLocation;

    private final Path PostsFileStorageLocation;

    private final String fileAccessUrlPrefix;

    public FileStorageService(
            @Value("${app.posts.file-upload-dir}") String uploadDir,
            @Value("${app.file-upload-dir}") String postUploadDir,
            @Value("${COMPOUND_BASE}") String fileAccessUrlPrefix) throws IOException {
        this.PostsFileStorageLocation = Paths.get(postUploadDir).toAbsolutePath().normalize();

        this.fileAccessUrlPrefix = fileAccessUrlPrefix;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        // Create directories if not exist
//        Files.createDirectories(this.fileStorageLocation); //todo
//        Files.createDirectories(this.PostsFileStorageLocation);
    }

    public String storeFile(MultipartFile file) {
        // Early validation
        if (file.isEmpty()) {
            throw new UnauthorizedException("Cannot store empty file");
        }

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String fileName = generateUniqueFileName(originalFileName);
        Path targetLocation = this.fileStorageLocation.resolve(fileName);

        try {
            // Use transferTo for better performance with large files
            storeFileEfficiently(file, targetLocation);

            logger.debug("Successfully stored file: {}", fileName);
            return fileName;

        } catch (IOException ex) {
            logger.error("Failed to store file: {}", fileName, ex);
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public String storeFilePosts(MultipartFile file) {
        // Early validation
        if (file.isEmpty()) {
            throw new UnauthorizedException("Cannot store empty file");
        }

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String fileName = generateUniqueFileName(originalFileName);
        Path targetLocation = this.PostsFileStorageLocation.resolve(fileName);

        try {
            // Use transferTo for better performance with large files
            storeFileEfficiently(file, targetLocation);

            logger.debug("Successfully stored file: {}", fileName);
            return fileName;

        } catch (IOException ex) {
            logger.error("Failed to store file: {}", fileName, ex);
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    private void storeFileEfficiently(MultipartFile file, Path targetLocation) throws IOException {
        // For small files, use the standard approach
        if (file.getSize() <= 1024 * 1024) { // 1MB threshold
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } else {
            // For larger files, use buffered streaming for better memory efficiency
            try (InputStream inputStream = file.getInputStream();
                 OutputStream outputStream = Files.newOutputStream(targetLocation,
                         StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                 BufferedOutputStream bufferedOut = new BufferedOutputStream(outputStream, BUFFER_SIZE)) {

                inputStream.transferTo(bufferedOut);
            }
        }
    }

    private String sanitizeFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return "unnamed_file";
        }

        // Remove potentially dangerous characters and normalize
        String sanitized = INVALID_CHARS_PATTERN.matcher(originalFilename.trim()).replaceAll("_");

        // Ensure reasonable length
        if (sanitized.length() > 100) {
            String extension = getFileExtension(sanitized);
            String baseName = sanitized.substring(0, 100 - extension.length());
            sanitized = baseName + extension;
        }

        return sanitized;
    }

    private String generateUniqueFileName(String originalName) {
        String fileExtension = getFileExtension(originalName);
        String baseName = getBaseName(originalName, fileExtension);

        // Use nano time + random for better uniqueness and performance
        long timestamp = System.nanoTime();
        int randomSuffix = SECURE_RANDOM.nextInt(10000);

        return String.format("%s_%d_%04d%s", baseName, timestamp, randomSuffix, fileExtension);
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex > 0 && dotIndex < filename.length() - 1)
                ? filename.substring(dotIndex)
                : "";
    }

    private String getBaseName(String filename, String extension) {
        if (extension.isEmpty()) {
            return filename;
        }
        return filename.substring(0, filename.length() - extension.length());
    }

//    private String buildFileUrl(String fileName) {
//        // More efficient string building for URLs
//        StringBuilder urlBuilder = new StringBuilder(fileAccessUrlPrefix.length() + fileName.length() + 1);
//        urlBuilder.append(fileAccessUrlPrefix);
//        if (!fileAccessUrlPrefix.endsWith("/")) {
//            urlBuilder.append("/");
//        }
//        urlBuilder.append(fileName);
//        return urlBuilder.toString();
//    }

    // Additional utility methods for better functionality

    public boolean deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

            // Security check: ensure file is within the storage directory
            if (!filePath.startsWith(this.fileStorageLocation)) {
                throw new SecurityException("Invalid file path");
            }

            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            logger.error("Failed to delete file: {}", fileName, ex);
            return false;
        }
    }

    public boolean fileExists(String fileName) {
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        return Files.exists(filePath) && filePath.startsWith(this.fileStorageLocation);
    }

    public long getFileSize(String fileName) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        if (!filePath.startsWith(this.fileStorageLocation)) {
            throw new SecurityException("Invalid file path");
        }
        return Files.size(filePath);
    }

    // Custom exception for better error handling
    public static class FileStorageException extends RuntimeException {
        public FileStorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}