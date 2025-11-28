package org.utj.asman.dto;

import java.time.LocalDate;

/**
 * DTO for Assignment Form PDF (Image 2)
 */
public class AssignmentFormPdfDto {
    // Computer Details
    private String assetTag;
    private String cpuSerial;
    private String model;
    private String manufacturer;
    private String processor;
    private String memory;
    private String hardDisk;
    private String dockStationSerial;
    private String monitorSerial;
    private String upsSerial;
    private String supplier;
    private LocalDate purchaseDate;
    
    // Assigned To
    private String facilityName;
    private String designation;
    private String project;
    
    // Issued By
    private String issuedByName;
    private String issuedByDesignation;
    
    // Logos
    private String logoMain;
    private String logoPartner1;
    private String logoPartner2;
    
    // Metadata
    private LocalDate issueDate;

    public AssignmentFormPdfDto() {
        this.issueDate = LocalDate.now();
        this.project = "USAID Tujenge Jamii";
    }

    // Getters and Setters
    public String getAssetTag() {
        return assetTag;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    public String getCpuSerial() {
        return cpuSerial;
    }

    public void setCpuSerial(String cpuSerial) {
        this.cpuSerial = cpuSerial;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getHardDisk() {
        return hardDisk;
    }

    public void setHardDisk(String hardDisk) {
        this.hardDisk = hardDisk;
    }

    public String getDockStationSerial() {
        return dockStationSerial;
    }

    public void setDockStationSerial(String dockStationSerial) {
        this.dockStationSerial = dockStationSerial;
    }

    public String getMonitorSerial() {
        return monitorSerial;
    }

    public void setMonitorSerial(String monitorSerial) {
        this.monitorSerial = monitorSerial;
    }

    public String getUpsSerial() {
        return upsSerial;
    }

    public void setUpsSerial(String upsSerial) {
        this.upsSerial = upsSerial;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getIssuedByName() {
        return issuedByName;
    }

    public void setIssuedByName(String issuedByName) {
        this.issuedByName = issuedByName;
    }

    public String getIssuedByDesignation() {
        return issuedByDesignation;
    }

    public void setIssuedByDesignation(String issuedByDesignation) {
        this.issuedByDesignation = issuedByDesignation;
    }

    public String getLogoMain() {
        return logoMain;
    }

    public void setLogoMain(String logoMain) {
        this.logoMain = logoMain;
    }

    public String getLogoPartner1() {
        return logoPartner1;
    }

    public void setLogoPartner1(String logoPartner1) {
        this.logoPartner1 = logoPartner1;
    }

    public String getLogoPartner2() {
        return logoPartner2;
    }

    public void setLogoPartner2(String logoPartner2) {
        this.logoPartner2 = logoPartner2;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
}