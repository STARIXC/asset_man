package org.utj.asman.dto;

import lombok.Data;

@Data
public class AssetEntryDTO {
    private Long facilityId; // The ID of the hospital selected in the app
    private String cpuSerial;
    private String cpuModel;
    private String monitorSerial;
    private String monitorModel;
    private String upsSerial;
    private String upsModel;
}