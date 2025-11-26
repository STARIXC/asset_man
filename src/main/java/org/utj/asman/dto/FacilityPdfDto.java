package org.utj.asman.dto;

import org.utj.asman.model.Facility;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object used for rendering the PDF view.
 * It contains the fields required by the Thymeleaf template.
 */
public class FacilityPdfDto {
    // Facility fields
    private Long id;
    private String facilityName;
    private String mflCode;
    private String countyName;
    private LocalDateTime createdAt;

    // CPU Specification fields
    private String manufacturer;
    private String model;
    private String processor;
    private String memory;
    private String hardDisk;
    private LocalDate purchaseDate;
    private String supplier;

    public FacilityPdfDto() {
    }

    public static FacilityPdfDto from(Facility facility) {
        FacilityPdfDto dto = new FacilityPdfDto();
        dto.id = facility.getId();
        dto.facilityName = facility.getFacilityName();
        dto.mflCode = facility.getMflCode();
        if (facility.getCounty() != null) {
            dto.countyName = facility.getCounty().getCountyName();
        }
        // createdAt was added to Facility entity
        try {
            dto.createdAt = (LocalDateTime) facility.getClass().getMethod("getCreatedAt").invoke(facility);
        } catch (Exception e) {
            // ignore if not present
        }
        // CPU spec – not linked yet, leave null or placeholder
        dto.manufacturer = null;
        dto.model = null;
        dto.processor = null;
        dto.memory = null;
        dto.hardDisk = null;
        dto.purchaseDate = null;
        dto.supplier = null;
        return dto;
    }

    // Getters (Lombok could be used, but explicit for clarity)
    public Long getId() {
        return id;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getMflCode() {
        return mflCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getProcessor() {
        return processor;
    }

    public String getMemory() {
        return memory;
    }

    public String getHardDisk() {
        return hardDisk;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public String getSupplier() {
        return supplier;
    }
}
