package org.utj.asman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utj.asman.model.AssetRecord;
import org.utj.asman.model.Facility;
import org.utj.asman.repository.AssetRecordRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing AssetRecord entities (individual computer allocations).
 */
@Service
public class AssetRecordService {

    private final AssetRecordRepository assetRecordRepository;
    private final CpuSpecificationService cpuSpecificationService;

    @Autowired
    public AssetRecordService(AssetRecordRepository assetRecordRepository, CpuSpecificationService cpuSpecificationService) {
        this.assetRecordRepository = assetRecordRepository;
        this.cpuSpecificationService = cpuSpecificationService;
    }

    /**
     * Retrieves all asset records.
     * @return A list of all asset records.
     */
    public List<AssetRecord> findAllAssets() {
        return assetRecordRepository.findAll();
    }

    /**
     * Retrieves an asset record by its ID.
     * @param id The ID of the asset.
     * @return An Optional containing the asset record, or empty if not found.
     */
    public Optional<AssetRecord> getAssetById(Long id) {
        return assetRecordRepository.findById(id);
    }

    /**
     * Saves a new asset record or updates an existing one.
     * @param assetRecord The asset record entity to save.
     * @return The saved asset record.
     * @throws IllegalStateException if an asset with the same CPU serial number already exists.
     */
    public AssetRecord saveAsset(AssetRecord assetRecord) {
        // Check for uniqueness of CPU serial number before saving a new asset
        if (assetRecord.getId() == null && assetRecordRepository.existsByCpuSerial(assetRecord.getCpuSerial())) {
            throw new IllegalStateException("An asset with CPU Serial " + assetRecord.getCpuSerial() + " already exists.");
        }
        return assetRecordRepository.save(assetRecord);
    }

    /**
     * Deletes an asset record by ID.
     * @param id The ID of the asset record to delete.
     */
    public void deleteAsset(Long id) {
        assetRecordRepository.deleteById(id);
    }

    /**
     * Finds all asset records allocated to a specific facility.
     * @param facilityId The ID of the facility.
     * @return A list of asset records.
     */
    public List<AssetRecord> findAssetsByFacilityId(Long facilityId) {
        return assetRecordRepository.findByFacilityId(facilityId);
    }

    /**
     * Searches for assets by matching a query against serial numbers or CPU model.
     * @param query The search term.
     * @return A list of matching asset records.
     */
    public List<AssetRecord> searchAssets(String query) {
        return assetRecordRepository.searchAssetRecords(query);
    }
}