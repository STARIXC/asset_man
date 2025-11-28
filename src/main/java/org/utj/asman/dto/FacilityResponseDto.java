package org.utj.asman.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * DTO for returning Facility data to the view.
 * Handles null values and provides safe defaults.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityResponseDto {

    private Long id;
    private String facilityName;
    private String mflCode;
    private Long countyId;
    private String countyName;
    private String countyCode;
    private String createdAt;

    /**
     * Returns county name with a default value if null
     */
    public String getCountyName() {
        return countyName != null ? countyName : "Not Assigned";
    }

    /**
     * Returns county code with a default value if null
     */
    public String getCountyCode() {
        return countyCode != null ? countyCode : "N/A";
    }

    /**
     * Returns facility name with a default value if null
     */
    public String getFacilityName() {
        return facilityName != null ? facilityName : "";
    }

    /**
     * Returns MFL code with a default value if null
     */
    public String getMflCode() {
        return mflCode != null ? mflCode : "";
    }
}