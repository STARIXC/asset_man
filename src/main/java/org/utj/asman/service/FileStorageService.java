package org.utj.asman.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.utj.asman.util.OSValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);

    private final Path storageDirectory;

    public FileStorageService(OSValidator osValidator) {
        String directoryPath;

        if (osValidator.isWindows()) {
            directoryPath = "C:\\ASMAN\\DO_NOT_DELETE\\Documents\\uploads";
        } else if (osValidator.isMac() || osValidator.isUnix()) {
            directoryPath = "/opt/HRH/DO_NOT_DELETE/Documents/uploads";
        } else if (osValidator.isSolaris()) {
            directoryPath = "/var/HRH/DO_NOT_DELETE/Documents/uploads";
        } else {
            throw new IllegalStateException("Unsupported operating system");
        }

        this.storageDirectory = Paths.get(directoryPath).toAbsolutePath().normalize();

        // Ensure the storage directory exists
        try {
            Files.createDirectories(this.storageDirectory);
        } catch (IOException e) {
            LOGGER.error("Failed to create storage directory: {}", e.getMessage());
            throw new IllegalStateException("Could not initialize storage directory", e);
        }
    }

    /**
     * Stores a file in the storage directory and returns the stored file's unique name.
     *
     * @param file MultipartFile to be stored.
     * @return String representing the unique file name.
     * @throws IOException If an error occurs during file storage.
     */
    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty. Cannot store an empty file.");
        }

        // Clean and normalize the file name
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = StringUtils.getFilenameExtension(originalFileName);

        // Generate a unique file name using UUID and file extension
        String uniqueFileName = UUID.randomUUID() + "." + fileExtension;

        // Resolve target file location
        Path targetLocation = storageDirectory.resolve(uniqueFileName);

        // Store the file
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        LOGGER.info("File stored: {}", targetLocation);
        return uniqueFileName;
    }

    /**
     * Deletes a file from the storage directory.
     *
     * @param fileName The name of the file to be deleted.
     * @throws IOException If an error occurs during file deletion.
     */
    public void deleteFile(String fileName) throws IOException {
        // Resolve the full file path
        Path filePath = storageDirectory.resolve(fileName).normalize();

        // Validate file path to prevent path traversal attacks
        if (!filePath.startsWith(storageDirectory)) {
            LOGGER.error("Attempt to access file outside storage directory: {}", filePath);
            throw new SecurityException("Invalid file path");
        }

        // Delete the file if it exists
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            LOGGER.info("File deleted: {}", filePath);
        } else {
            LOGGER.warn("File not found for deletion: {}", filePath);
        }
    }

    /**
     * Retrieves the absolute path of a file in the storage directory.
     *
     * @param fileName The name of the file to be retrieved.
     * @return The absolute path of the file.
     */
    public String getFilePath(String fileName) {
        // Resolve the file path
        Path filePath = storageDirectory.resolve(fileName).normalize();

        // Validate the file path
        if (!filePath.startsWith(storageDirectory)) {
            LOGGER.error("Attempt to access file outside storage directory: {}", filePath);
            throw new SecurityException("Invalid file path");
        }

        return filePath.toString();
    }


    public Path getStorageDirectory() {
        return storageDirectory;
    }
}