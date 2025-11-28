package org.utj.asman.service;

import org.utj.asman.dto.AssetEntryDTO;
import org.utj.asman.model.*;
import org.utj.asman.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRecordRepository assetRepository;
    @Autowired
    private FacilityRepository facilityRepository;
    @Autowired
    private CpuSpecificationRepository cpuSpecRepository;

    // --- Read Operations ---
    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public List<CpuSpecification> getAllCpuSpecs() {
        // Assuming you added findAllByOrderByModelNameAsc in the repo, otherwise use findAll()
        return cpuSpecRepository.findAll();
    }

    public List<AssetRecord> getAssetRecords(Long facilityId) {
        if (facilityId != null) {
            return assetRepository.findByFacilityId(facilityId);
        }
        return assetRepository.findAll();
    }

    public List<String> searchModels(String type, String query) {
        if (type == null || query == null) return Collections.emptyList();
        if ("monitor".equalsIgnoreCase(type)) return assetRepository.searchMonitorModels(query);
        if ("ups".equalsIgnoreCase(type)) return assetRepository.searchUpsModels(query);
        return Collections.emptyList();
    }

    // --- Write Operations ---
    @Transactional
    public AssetRecord saveAsset(AssetEntryDTO request) {
        Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        CpuSpecification cpuSpec = cpuSpecRepository.findById(request.getCpuSpecId())
                .orElseThrow(() -> new RuntimeException("CPU Spec not found"));

        validateSerials(request);

        AssetRecord newRecord = new AssetRecord();
        newRecord.setFacility(facility);
        newRecord.setCpuSpecification(cpuSpec);

        mapDtoToEntity(request, newRecord);

        return assetRepository.save(newRecord);
    }

    @Transactional
    public AssetRecord updateAsset(Long id, AssetEntryDTO request) {
        AssetRecord record = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        // Update facility if changed
        if (!record.getFacility().getId().equals(request.getFacilityId())) {
            record.setFacility(facilityRepository.findById(request.getFacilityId())
                    .orElseThrow(() -> new RuntimeException("Facility not found")));
        }

        // Update CPU specification if it's null in database or if a new value is provided
        if (record.getCpuSpecification() == null || request.getCpuSpecId() != null) {
            CpuSpecification newCpuSpec = cpuSpecRepository.findById(request.getCpuSpecId())
                    .orElseThrow(() -> new RuntimeException("CPU Spec not found"));
            record.setCpuSpecification(newCpuSpec);
        }

        // Check if the NEW serial is already taken by ANOTHER record
        if (assetRepository.existsByCpuSerialAndIdNot(request.getCpuSerial(), id)) {
            throw new IllegalArgumentException("CPU Serial " + request.getCpuSerial() + " is already assigned to another asset.");
        }
        if (request.getMonitorSerial() != null && !request.getMonitorSerial().isEmpty() &&
                assetRepository.existsByMonitorSerialAndIdNot(request.getMonitorSerial(), id)) {
            throw new IllegalArgumentException("Monitor Serial " + request.getMonitorSerial() + " is already assigned to another asset.");
        }
        // Only check UPS serial if a non-null value is provided
        if (request.getUpsSerial() != null && !request.getUpsSerial().isEmpty() &&
                assetRepository.existsByUpsSerialAndIdNot(request.getUpsSerial(), id)) {
            throw new IllegalArgumentException("UPS Serial " + request.getUpsSerial() + " is already assigned to another asset.");
        }
        
        mapDtoToEntity(request, record);
        return assetRepository.save(record);
    }

    private void mapDtoToEntity(AssetEntryDTO dto, AssetRecord entity) {

        entity.setCpuSerial(dto.getCpuSerial());
        entity.setMonitorSerial(dto.getMonitorSerial());
        entity.setMonitorModel(dto.getMonitorModel());
        entity.setUpsSerial(dto.getUpsSerial());
        entity.setUpsModel(dto.getUpsModel());
        // Note: AssetTag logic can be added here if needed
    }

    private void validateSerials(AssetEntryDTO request) {
        if (assetRepository.existsByCpuSerial(request.getCpuSerial())) {
            throw new IllegalArgumentException("CPU Serial exists: " + request.getCpuSerial());
        }
        if (request.getMonitorSerial() != null && !request.getMonitorSerial().isEmpty() &&
                assetRepository.existsByMonitorSerial(request.getMonitorSerial())) {
            throw new IllegalArgumentException("Monitor Serial exists: " + request.getMonitorSerial());
        }
        if (request.getUpsSerial() != null && !request.getUpsSerial().isEmpty() &&
                assetRepository.existsByUpsSerial(request.getUpsSerial())) {
            throw new IllegalArgumentException("UPS Serial exists: " + request.getUpsSerial());
        }
    }

    @Transactional
    public void deleteAsset(Long id) {
        AssetRecord record = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found with ID: " + id));
        assetRepository.delete(record);
    }
}