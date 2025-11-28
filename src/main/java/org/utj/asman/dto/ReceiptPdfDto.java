package org.utj.asman.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Receipt PDF (Image 1)
 */
public class ReceiptPdfDto {
    private Long facilityId;
    private String facilityName;
    private String mflCode;
    private String countyName;
    private List<AssetEquipmentDto> equipmentList;
    private String orgName;
    private String orgAddress;
    private LocalDateTime generatedDate;

    // Logos
    private String logoMain;
    private String logoPartner1;
    private String logoPartner2;

    // Signatory information
    private String signatoryName;
    private String signatoryTitle;
    private String signatoryOrganization;

    public ReceiptPdfDto() {
        this.generatedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getMflCode() {
        return mflCode;
    }

    public void setMflCode(String mflCode) {
        this.mflCode = mflCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public List<AssetEquipmentDto> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<AssetEquipmentDto> equipmentList) {
        this.equipmentList = equipmentList;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgAddress() {
        return orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
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

    public String getSignatoryName() {
        return signatoryName;
    }

    public void setSignatoryName(String signatoryName) {
        this.signatoryName = signatoryName;
    }

    public String getSignatoryTitle() {
        return signatoryTitle;
    }

    public void setSignatoryTitle(String signatoryTitle) {
        this.signatoryTitle = signatoryTitle;
    }

    public String getSignatoryOrganization() {
        return signatoryOrganization;
    }

    public void setSignatoryOrganization(String signatoryOrganization) {
        this.signatoryOrganization = signatoryOrganization;
    }
}