package org.utj.asman.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO for creating or updating a Facility (Java 8 Compatible).
 * Uses standard Java class structure with Lombok and javax.validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDto {

    private Long id; // Null for create, required for update

    @NotBlank(message = "Facility name is required.")
    @Size(max = 255, message = "Facility name cannot exceed 255 characters.")
    private String facilityName;

    @NotBlank(message = "MFL Code is required.")
    @Size(max = 50, message = "MFL Code cannot exceed 50 characters.")
    private String mflCode;
}