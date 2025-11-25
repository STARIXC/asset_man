package org.utj.asman.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetEntryDTO {

    private Long id; // Null for creation

    @NotNull(message = "Facility ID is required")
    private Long facilityId;

    // We use the ID of the selected CPU Specification from the dropdown
    @NotNull(message = "CPU Specification ID is required")
    private Long cpuSpecId;

    @NotBlank(message = "CPU Serial is required")
    @Size(max = 100, message = "CPU Serial too long")
    private String cpuSerial;

    // Monitor details
    @Size(max = 100)
    private String monitorSerial;

    @Size(max = 100)
    private String monitorModel;

    // UPS details
    @Size(max = 100)
    private String upsSerial;

    @Size(max = 100)
    private String upsModel;

    // Asset Tag
    @Size(max = 50)
    private String assetTag;
}