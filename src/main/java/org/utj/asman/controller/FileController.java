package org.utj.asman.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.utj.asman.service.FileStorageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Controller for serving uploaded files from external storage directory.
 * This is necessary for production Tomcat environments where static resource
 * handlers may not properly serve files from external directories.
 */
@Controller
@RequestMapping("/uploads")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Serve an uploaded file by filename.
     * This endpoint serves files from the external storage directory configured
     * in FileStorageService, making them accessible via HTTP in both development
     * and production environments.
     *
     * @param filename The name of the file to serve
     * @return ResponseEntity containing the file resource
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // Get the file path from storage service
            Path filePath = fileStorageService.getStorageDirectory().resolve(filename).normalize();

            // Security check: ensure the file is within the storage directory
            if (!filePath.startsWith(fileStorageService.getStorageDirectory())) {
                log.warn("Attempted to access file outside storage directory: {}", filename);
                return ResponseEntity.badRequest().build();
            }

            // Check if file exists
            if (!Files.exists(filePath)) {
                log.warn("File not found: {}", filename);
                return ResponseEntity.notFound().build();
            }

            // Load file as Resource
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.warn("File not readable: {}", filename);
                return ResponseEntity.notFound().build();
            }

            // Determine content type
            String contentType = null;
            try {
                contentType = Files.probeContentType(filePath);
            } catch (IOException e) {
                log.warn("Could not determine file type for: {}", filename);
            }

            // Fallback to default content type if type could not be determined
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            log.debug("Serving file: {} with content type: {}", filename, contentType);

            // Return the file with appropriate headers
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            log.error("Malformed URL for file: {}", filename, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error serving file: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
