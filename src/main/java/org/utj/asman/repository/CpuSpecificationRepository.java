package org.utj.asman.repository;

import org.utj.asman.model.CpuSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing CpuSpecification entities.
 * Extends JpaRepository to inherit standard CRUD operations.
 * This repository is key for ensuring unique hardware specs are reused across many AssetRecords.
 */
public interface CpuSpecificationRepository extends JpaRepository<CpuSpecification, Long> {

    /**
     * Finds a CpuSpecification by the key combination of its processor, memory, and hard disk.
     * This is useful for checking if a new specification entry already exists before creation.
     * @param processor The processor string (e.g., "Intel® Core™ i5 10Th Gen").
     * @param memory The memory string (e.g., "8.00 GB").
     * @param hardDisk The hard disk string (e.g., "1000 GB").
     * @return An Optional containing the found CpuSpecification or empty.
     */
    Optional<CpuSpecification> findByProcessorAndMemoryAndHardDisk(
            String processor,
            String memory,
            String hardDisk);

    /**
     * Checks if a CpuSpecification with the exact combination of processor, memory, and hard disk exists.
     * @param processor The processor string.
     * @param memory The memory string.
     * @param hardDisk The hard disk string.
     * @return True if the combination exists, false otherwise.
     */
    boolean existsByProcessorAndMemoryAndHardDisk(
            String processor,
            String memory,
            String hardDisk);
}