package org.utj.asman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.utj.asman.dto.FacilityDto;
import org.utj.asman.dto.FacilityResponseDto;
import org.utj.asman.util.FacilityMapper;
import org.utj.asman.model.Facility;
import org.utj.asman.model.County;
import org.utj.asman.repository.FacilityRepository;
import org.utj.asman.repository.CountyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private CountyRepository countyRepository;

    @Autowired
    private FacilityMapper facilityMapper;

    /**
     * Get all facilities as DTOs with null-safe handling
     * This method ensures all null values are handled before reaching the view
     */
    public List<FacilityResponseDto> getAllFacilitiesDto() {
        List<Facility> facilities = facilityRepository.findAll();
        return facilities.stream()
                .map(facilityMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get facility by ID as DTO with null-safe handling
     * Returns empty Optional if facility not found
     */
    public Optional<FacilityResponseDto> getFacilityDtoById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        
        return facilityRepository.findById(id)
                .map(facilityMapper::toResponseDto);
    }

    /**
     * Save or update facility using DTO
     * Handles both create and update operations
     * 
     * @param facilityDto The facility data to save
     * @param countyId The county ID to assign (can be null)
     * @return Saved facility as DTO with all null values handled
     */
    public FacilityResponseDto saveFacility(FacilityDto facilityDto, Long countyId) {
        if (facilityDto == null) {
            throw new IllegalArgumentException("Facility DTO cannot be null");
        }

        Facility facility;

        if (facilityDto.getId() != null) {
            // Update existing facility
            facility = facilityRepository.findById(facilityDto.getId())
                    .orElseThrow(() -> new RuntimeException("Facility not found with ID: " + facilityDto.getId()));
            
            // Update fields from DTO
            facilityMapper.updateEntityFromDto(facility, facilityDto);
        } else {
            // Create new facility
            facility = facilityMapper.toEntity(facilityDto);
        }

        // Handle county assignment with null-safety
        if (countyId != null && countyId > 0) {
            County county = countyRepository.findById(countyId)
                    .orElse(null); // Set to null if county not found rather than throwing exception
            facility.setCounty(county);
        } else {
            facility.setCounty(null);
        }

        // Save and convert to DTO
        Facility savedFacility = facilityRepository.save(facility);
        return facilityMapper.toResponseDto(savedFacility);
    }

    /**
     * Delete facility by ID
     * 
     * @param id The facility ID to delete
     * @throws RuntimeException if facility not found
     */
    public void deleteFacility(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Facility ID cannot be null");
        }
        
        if (!facilityRepository.existsById(id)) {
            throw new RuntimeException("Facility not found with ID: " + id);
        }
        
        facilityRepository.deleteById(id);
    }

    /**
     * Check if MFL code already exists (for validation)
     * 
     * @param mflCode The MFL code to check
     * @param excludeFacilityId Facility ID to exclude from check (for updates)
     * @return true if MFL code exists, false otherwise
     */
    public boolean mflCodeExists(String mflCode, Long excludeFacilityId) {
        if (mflCode == null || mflCode.trim().isEmpty()) {
            return false;
        }

        Optional<Facility> existing = facilityRepository.findByMflCode(mflCode);
        
        if (!existing.isPresent()) {
            return false;
        }

        // If we're excluding a facility ID (for updates), check if it's the same facility
        if (excludeFacilityId != null) {
            return !existing.get().getId().equals(excludeFacilityId);
        }

        return true;
    }

    /**
     * Get all facilities by county ID as DTOs
     * 
     * @param countyId The county ID
     * @return List of facilities in the county
     */
    public List<FacilityResponseDto> getFacilitiesByCountyId(Long countyId) {
        if (countyId == null) {
            return getAllFacilitiesDto();
        }

        List<Facility> facilities = facilityRepository.findByCountyId(countyId);
        return facilities.stream()
                .map(facilityMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Search facilities by name or MFL code
     * 
     * @param searchTerm The search term
     * @return List of matching facilities as DTOs
     */
    public List<FacilityResponseDto> searchFacilities(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllFacilitiesDto();
        }

        String normalizedSearch = searchTerm.trim().toLowerCase();
        List<Facility> facilities = facilityRepository.findAll();
        
        return facilities.stream()
                .filter(f -> 
                    (f.getFacilityName() != null && f.getFacilityName().toLowerCase().contains(normalizedSearch)) ||
                    (f.getMflCode() != null && f.getMflCode().toLowerCase().contains(normalizedSearch))
                )
                .map(facilityMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Count total facilities
     * 
     * @return Total number of facilities
     */
    public long countFacilities() {
        return facilityRepository.count();
    }

    /**
     * Count facilities by county
     * 
     * @param countyId The county ID
     * @return Number of facilities in the county
     */
    public long countFacilitiesByCounty(Long countyId) {
        if (countyId == null) {
            return 0;
        }
        return facilityRepository.countByCountyId(countyId);
    }

    // ============================================
    // Legacy methods for backward compatibility
    // These methods work with entities directly
    // Consider migrating to DTO methods above
    // ============================================

    /**
     * @deprecated Use getAllFacilitiesDto() instead
     */
    @Deprecated
    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    /**
     * @deprecated Use getFacilityDtoById() instead
     */
    @Deprecated
    public Optional<Facility> getFacilityById(Long id) {
        return facilityRepository.findById(id);
    }

    /**
     * @deprecated Use saveFacility(FacilityDto, Long) instead
     */
    @Deprecated
    public Facility updateFacility(Long id, Facility facilityDetails) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        if (facilityDetails.getFacilityName() != null) {
            facility.setFacilityName(facilityDetails.getFacilityName());
        }
        
        if (facilityDetails.getMflCode() != null) {
            facility.setMflCode(facilityDetails.getMflCode());
        }
        
        if (facilityDetails.getCounty() != null) {
            facility.setCounty(facilityDetails.getCounty());
        }
        
        return facilityRepository.save(facility);
    }
}