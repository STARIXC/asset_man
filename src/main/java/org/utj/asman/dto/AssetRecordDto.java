package org.utj.asman.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO for creating or updating an AssetRecord (Java 8 Compatible).
 * Flattens the input for asset creation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetRecordDto {

        private Long id; // Null for creation

        @NotNull(message = "Facility ID must be provided.")
        private Long facilityId;

        // LocalDateTime is part of Java 8
        private LocalDateTime allocationDate; // Managed by the service/entity lifecycle

        // --- CPU Details (Required and unique identifiers) ---
        @NotBlank(message = "CPU Serial number is required.")
        @Size(max = 255, message = "CPU Serial cannot exceed 255 characters.")
        private String cpuSerial;

        @NotBlank(message = "CPU Model is required.")
        @Size(max = 255, message = "CPU Model cannot exceed 255 characters.")
        private String cpuModel;

        // --- CPU Specification Details (for normalization) ---
        @NotBlank(message = "Processor specification is required.")
        private String processor;

        @NotBlank(message = "Memory specification is required.")
        private String memory;

        @NotBlank(message = "Hard Disk specification is required.")
        private String hardDisk;

        // --- Monitor Details (Optional) ---
        @Size(max = 255, message = "Monitor Serial cannot exceed 255 characters.")
        private String monitorSerial;

        @Size(max = 255, message = "Monitor Model cannot exceed 255 characters.")
        private String monitorModel;

        // --- UPS Details (Optional) ---
        @Size(max = 255, message = "UPS Serial cannot exceed 255 characters.")
        private String upsSerial;

        @Size(max = 255, message = "UPS Model cannot exceed 255 characters.")
        private String upsModel;
}