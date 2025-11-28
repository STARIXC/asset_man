package org.utj.asman.util;

import org.springframework.stereotype.Component;
import org.utj.asman.dto.FacilityDto;
import org.utj.asman.dto.FacilityResponseDto;
import org.utj.asman.model.Facility;
import org.utj.asman.model.County;

import java.time.format.DateTimeFormatter;

/**
 * Mapper class for converting between Facility entities and DTOs
 * Handles all null values to ensure safe data transfer to the view
 */
@Component
public class FacilityMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Convert Facility entity to FacilityResponseDto
     * Handles all null values safely - this is where we prevent null pointer exceptions
     * 
     * @param facility The facility entity (can be null)
     * @return FacilityResponseDto with safe default values, or null if input is null
     */
    public FacilityResponseDto toResponseDto(Facility facility) {
        if (facility == null) {
            return null;
        }

        FacilityResponseDto.FacilityResponseDtoBuilder builder = FacilityResponseDto.builder()
                .id(facility.getId())
                .facilityName(facility.getFacilityName() != null ? facility.getFacilityName() : "")
                .mflCode(facility.getMflCode() != null ? facility.getMflCode() : "");

        // Handle county null-safety
        // County might be null or have null fields
        if (facility.getCounty() != null) {
            County county = facility.getCounty();
            builder.countyId(county.getId())
                   .countyName(county.getCountyName() != null ? county.getCountyName() : "Not Assigned")
                   .countyCode(county.getCountyCode() != null ? county.getCountyCode() : "N/A");
        } else {
            // If no county is assigned, use default values
            builder.countyId(null)
                   .countyName("Not Assigned")
                   .countyCode("N/A");
        }

        // Handle createdAt null-safety
        if (facility.getCreatedAt() != null) {
            builder.createdAt(facility.getCreatedAt().format(DATE_FORMATTER));
        } else {
            builder.createdAt("");
        }

        return builder.build();
    }

    /**
     * Convert FacilityDto to Facility entity (for creating new facilities)
     * 
     * @param dto The facility DTO (can be null)
     * @return New Facility entity, or null if input is null
     */
    public Facility toEntity(FacilityDto dto) {
        if (dto == null) {
            return null;
        }

        Facility facility = new Facility();
        facility.setId(dto.getId());
        facility.setFacilityName(dto.getFacilityName());
        facility.setMflCode(dto.getMflCode());
        // County will be set separately in the service layer

        return facility;
    }

    /**
     * Update existing Facility entity from FacilityDto
     * Only updates non-null fields from the DTO
     * 
     * @param facility The existing facility entity to update
     * @param dto The DTO containing updated values
     */
    public void updateEntityFromDto(Facility facility, FacilityDto dto) {
        if (facility == null || dto == null) {
            return;
        }

        // Only update if the DTO has non-null values
        if (dto.getFacilityName() != null && !dto.getFacilityName().trim().isEmpty()) {
            facility.setFacilityName(dto.getFacilityName());
        }
        
        if (dto.getMflCode() != null && !dto.getMflCode().trim().isEmpty()) {
            facility.setMflCode(dto.getMflCode());
        }
        
        // Note: County is handled separately in the service layer
        // because it requires a repository lookup
    }

    /**
     * Convert Facility entity to FacilityDto (for editing)
     * Useful when you need to populate the form with existing data
     * 
     * @param facility The facility entity
     * @return FacilityDto with the entity's current values
     */
    public FacilityDto toDto(Facility facility) {
        if (facility == null) {
            return null;
        }

        FacilityDto dto = new FacilityDto();
        dto.setId(facility.getId());
        dto.setFacilityName(facility.getFacilityName());
        dto.setMflCode(facility.getMflCode());

        return dto;
    }
}