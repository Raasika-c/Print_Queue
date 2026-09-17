package com.printqueue.service;

import com.printqueue.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    
    private static final List<String> ALLOWED_EXTENSIONS = List.of("pdf", "doc", "docx", "txt");
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
    );

    public FileStorageService(@Value("${app.file.upload-dir:./uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Failed to store empty file");
        }
        
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        String extension = getExtension(originalFilename).toLowerCase();
        
        if (!ALLOWED_EXTENSIONS.contains(extension) && !ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Invalid file type. Supported types: PDF, DOC, DOCX, TXT");
        }
        
        if (originalFilename.contains("..")) {
            throw new BadRequestException("Filename contains invalid path sequence " + originalFilename);
        }
        
        try {
            String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path targetLocation = this.fileStorageLocation.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }
    
    public int calculatePageCount(MultipartFile file) {
        // Mock page count calculation for academic project
        // In a real app, use Apache PDFBox or Apache POI
        long size = file.getSize();
        if (size == 0) {
            throw new BadRequestException("Invalid pages: File is empty");
        }
        int pages = (int) Math.max(1, size / (50 * 1024)); // roughly 1 page per 50KB
        if (pages > 500) {
            throw new BadRequestException("Invalid pages: Exceeds maximum 500 pages");
        }
        return pages;
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }
}
