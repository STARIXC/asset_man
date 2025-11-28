package org.utj.asman.dto;

/**
 * Equipment item for receipt
 */
public class AssetEquipmentDto {
    private String equipment;
    private String serialNumber;

    public AssetEquipmentDto() {
    }

    public AssetEquipmentDto(String equipment, String serialNumber) {
        this.equipment = equipment;
        this.serialNumber = serialNumber;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}