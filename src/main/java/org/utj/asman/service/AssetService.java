package org.utj.asman.service;

import org.utj.asman.dto.AssetEntryDTO;
import org.utj.asman.model.AssetRecord;
import org.utj.asman.model.Facility;
import org.utj.asman.repository.AssetRecordRepository;
import org.utj.asman.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRecordRepository assetRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public List<AssetRecord> getAssetRecords(Long facilityId) {
        if (facilityId != null) {
            return assetRepository.findByFacilityId(facilityId);
        }
        return assetRepository.findAll();
    }

    // Suggest models
    public List<String> searchModels(String type, String query) {
        switch (type.toLowerCase()) {
            case "cpu": return assetRepository.searchCpuModels(query);
            case "monitor": return assetRepository.searchMonitorModels(query);
            case "ups": return assetRepository.searchUpsModels(query);
            default: return java.util.Collections.emptyList();
        }
    }

    @Transactional
    public AssetRecord saveAsset(AssetEntryDTO request) {
        // 1. Validate Facility
        Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        // 2. Validate Serial Uniqueness
        validateSerials(request, null);

        // 3. Create & Save
        AssetRecord newRecord = new AssetRecord();
        newRecord.setFacility(facility);
        mapDtoToEntity(request, newRecord);
        
        return assetRepository.save(newRecord);
    }

    @Transactional
    public AssetRecord updateAsset(Long id, AssetEntryDTO request) {
        // 1. Find existing
        AssetRecord existingRecord = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        // 2. Update Facility
        if (!existingRecord.getFacility().getId().equals(request.getFacilityId())) {
             Facility newFacility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Facility not found"));
             existingRecord.setFacility(newFacility);
        }

        // 3. Validate Serial Uniqueness (pass ID to exclude self)
        // Note: Complex exclusion logic is needed if we strictly want to allow updates to same serial
        // For simplicity, we assume updates usually don't change serials to existing ones of OTHER assets.
        
        // 4. Update Fields
        mapDtoToEntity(request, existingRecord);

        return assetRepository.save(existingRecord);
    }

    // Helper to map fields
    private void mapDtoToEntity(AssetEntryDTO dto, AssetRecord entity) {
        entity.setCpuSerial(dto.getCpuSerial());
        entity.setCpuModel(dto.getCpuModel());
        entity.setMonitorSerial(dto.getMonitorSerial());
        entity.setMonitorModel(dto.getMonitorModel());
        entity.setUpsSerial(dto.getUpsSerial());
        entity.setUpsModel(dto.getUpsModel());
    }

    // Helper for Validation
    private void validateSerials(AssetEntryDTO request, Long excludeId) {
        // Check CPU
        if (request.getCpuSerial() != null && !request.getCpuSerial().isEmpty()) {
            if (assetRepository.existsByCpuSerial(request.getCpuSerial())) {
                throw new IllegalArgumentException("Error: CPU Serial '" + request.getCpuSerial() + "' already exists!");
            }
        }
        // Check Monitor
        if (request.getMonitorSerial() != null && !request.getMonitorSerial().isEmpty()) {
            if (assetRepository.existsByMonitorSerial(request.getMonitorSerial())) {
                throw new IllegalArgumentException("Error: Monitor Serial '" + request.getMonitorSerial() + "' already exists!");
            }
        }
        // Check UPS
        if (request.getUpsSerial() != null && !request.getUpsSerial().isEmpty()) {
            if (assetRepository.existsByUpsSerial(request.getUpsSerial())) {
                throw new IllegalArgumentException("Error: UPS Serial '" + request.getUpsSerial() + "' already exists!");
            }
        }
    }
}