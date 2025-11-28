package org.utj.asman.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.utj.asman.dto.AssignmentFormPdfDto;
import org.utj.asman.dto.AssetEquipmentDto;
import org.utj.asman.dto.FacilityResponseDto;
import org.utj.asman.dto.ReceiptPdfDto;
import org.utj.asman.model.AssetRecord;
import org.utj.asman.model.Facility;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnhancedPdfService {

    private static final Logger log = LoggerFactory.getLogger(EnhancedPdfService.class);

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private SettingService settingService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FacilityService facilityService;

    /**
     * Generate Receipt PDF (Image 1 style)
     * NOW WITH DTO PATTERN - NO MORE LAZY INITIALIZATION ERRORS!
     */
    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateReceiptPdf(Long facilityId, List<AssetRecord> assets) {
        try {
            // Use DTO service to get facility with all data loaded safely
            FacilityResponseDto facilityDto = facilityService.getFacilityDtoById(facilityId)
                    .orElseThrow(() -> new RuntimeException("Facility not found with ID: " + facilityId));

            ReceiptPdfDto dto = buildReceiptDto(facilityDto, assets);

            Context context = new Context();
            context.setVariable("receipt", dto);

            String htmlContent = templateEngine.process("pdf/receipt", context);

            return renderPdf(htmlContent);

        } catch (Exception e) {
            log.error("Error generating receipt PDF for facility: {}", facilityId, e);
            throw new RuntimeException("Failed to generate receipt PDF", e);
        }
    }

    /**
     * OVERLOADED METHOD - For backward compatibility
     * Converts Facility entity to DTO first
     */
    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateReceiptPdf(Facility facility, List<AssetRecord> assets) {
        if (facility == null || facility.getId() == null) {
            throw new IllegalArgumentException("Facility and facility ID cannot be null");
        }
        return generateReceiptPdf(facility.getId(), assets);
    }

    /**
     * Generate Assignment Form PDF (Image 2 style)
     */
    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateAssignmentFormPdf(AssetRecord asset) {
        try {
            AssignmentFormPdfDto dto = buildAssignmentFormDto(asset);

            Context context = new Context();
            context.setVariable("form", dto);

            String htmlContent = templateEngine.process("pdf/assignment_form", context);

            return renderPdf(htmlContent);

        } catch (Exception e) {
            log.error("Error generating assignment form PDF for asset: {}", asset.getId(), e);
            throw new RuntimeException("Failed to generate assignment form PDF", e);
        }
    }

    /**
     * Generate multiple assignment forms for a facility
     */
    @Transactional(readOnly = true)
    public ByteArrayOutputStream generateBulkAssignmentForms(List<AssetRecord> assets) {
        try {
            List<AssignmentFormPdfDto> forms = new ArrayList<AssignmentFormPdfDto>();
            for (AssetRecord asset : assets) {
                forms.add(buildAssignmentFormDto(asset));
            }

            Context context = new Context();
            context.setVariable("forms", forms);

            String htmlContent = templateEngine.process("pdf/assignment_forms_bulk", context);

            return renderPdf(htmlContent);

        } catch (Exception e) {
            log.error("Error generating bulk assignment forms PDF", e);
            throw new RuntimeException("Failed to generate bulk assignment forms PDF", e);
        }
    }

    /**
     * Build ReceiptPdfDto from FacilityResponseDto (DTO) and assets
     * NO MORE LAZY INITIALIZATION ERRORS - All data is already loaded in the DTO!
     */
    private ReceiptPdfDto buildReceiptDto(FacilityResponseDto facilityDto, List<AssetRecord> assets) {
        ReceiptPdfDto dto = new ReceiptPdfDto();

        // Facility info - safely from DTO (no lazy loading issues)
        dto.setFacilityId(facilityDto.getId());
        dto.setFacilityName(facilityDto.getFacilityName()); // Already has default ""
        dto.setMflCode(facilityDto.getMflCode()); // Already has default ""
        dto.setCountyName(facilityDto.getCountyName()); // Already has default "Not Assigned"

        // Equipment list
        List<AssetEquipmentDto> equipmentList = new ArrayList<AssetEquipmentDto>();
        for (AssetRecord asset : assets) {
            if (asset.getCpuSerial() != null && !asset.getCpuSerial().trim().isEmpty()) {
                equipmentList.add(new AssetEquipmentDto("CPU (" + asset.getCpuSpecification().getManufacturer() + " "
                        + asset.getCpuSpecification().getModel() + ")", asset.getCpuSerial()));
            }
            if (asset.getMonitorSerial() != null && !asset.getMonitorSerial().trim().isEmpty()) {
                String monitorModel = (asset.getMonitorModel() != null && !asset.getMonitorModel().trim().isEmpty())
                        ? asset.getMonitorModel()
                        : "N/A";
                equipmentList.add(new AssetEquipmentDto("Monitor (" + monitorModel + ")", asset.getMonitorSerial()));
            }
            if (asset.getUpsSerial() != null && !asset.getUpsSerial().trim().isEmpty()) {
                String upsModel = (asset.getUpsModel() != null && !asset.getUpsModel().trim().isEmpty())
                        ? asset.getUpsModel()
                        : "N/A";
                equipmentList.add(new AssetEquipmentDto("UPS (" + upsModel + ")", asset.getUpsSerial()));
            }
        }
        dto.setEquipmentList(equipmentList);

        // Organization settings
        dto.setOrgName(settingService.getSettingValue("org_name", "USAID Tujenge Jamii"));
        dto.setOrgAddress(settingService.getSettingValue("org_address", ""));

        // Logos
        dto.setLogoMain(getLogoPath("logo_main"));
        dto.setLogoPartner1(getLogoPath("logo_partner1"));
        dto.setLogoPartner2(getLogoPath("logo_partner2"));

        // Signatory information
        dto.setSignatoryName(settingService.getSettingValue("chief_of_party_name", "Dr. Moses Kitheka"));
        dto.setSignatoryTitle(settingService.getSettingValue("chief_of_party_title", "Chief of Party"));
        dto.setSignatoryOrganization(settingService.getSettingValue("org_name", "USAID Tujenge Jamii"));

        return dto;
    }

    /**
     * Build AssignmentFormPdfDto from asset record
     * Uses @Transactional to ensure lazy-loaded relationships are available
     */
    private AssignmentFormPdfDto buildAssignmentFormDto(AssetRecord asset) {
        AssignmentFormPdfDto dto = new AssignmentFormPdfDto();

        // Asset details with null safety
        dto.setAssetTag(asset.getAssetTag() != null ? asset.getAssetTag() : "");
        dto.setCpuSerial(asset.getCpuSerial() != null ? asset.getCpuSerial() : "");
        dto.setMonitorSerial(asset.getMonitorSerial() != null ? asset.getMonitorSerial() : "");
        dto.setUpsSerial(asset.getUpsSerial() != null ? asset.getUpsSerial() : "");

        // CPU Specification details with null safety
        if (asset.getCpuSpecification() != null) {
            dto.setManufacturer(asset.getCpuSpecification().getManufacturer() != null
                    ? asset.getCpuSpecification().getManufacturer()
                    : "");
            dto.setModel(asset.getCpuSpecification().getModel() != null
                    ? asset.getCpuSpecification().getModel()
                    : "");
            dto.setProcessor(asset.getCpuSpecification().getProcessor() != null
                    ? asset.getCpuSpecification().getProcessor()
                    : "");
            dto.setMemory(asset.getCpuSpecification().getMemory() != null
                    ? asset.getCpuSpecification().getMemory()
                    : "");
            dto.setHardDisk(asset.getCpuSpecification().getHardDisk() != null
                    ? asset.getCpuSpecification().getHardDisk()
                    : "");
            dto.setPurchaseDate(asset.getCpuSpecification().getPurchaseDate());
            dto.setSupplier(asset.getCpuSpecification().getSupplier() != null
                    ? asset.getCpuSpecification().getSupplier()
                    : "");
        }

        // Facility info with null safety - access within transaction
        if (asset.getFacility() != null) {
            // If you have FacilityService, use DTO approach here too
            if (asset.getFacility().getId() != null) {
                FacilityResponseDto facilityDto = facilityService.getFacilityDtoById(asset.getFacility().getId())
                        .orElse(null);
                if (facilityDto != null) {
                    dto.setFacilityName(facilityDto.getFacilityName());
                    dto.setDesignation(facilityDto.getFacilityName());
                }
            } else {
                // Fallback to direct access (within transaction, so it's safe)
                dto.setFacilityName(asset.getFacility().getFacilityName() != null
                        ? asset.getFacility().getFacilityName()
                        : "");
                dto.setDesignation(asset.getFacility().getFacilityName() != null
                        ? asset.getFacility().getFacilityName()
                        : "");
            }
        }

        // Issued by (from settings or hardcoded)
        dto.setIssuedByName(settingService.getSettingValue("issued_by_name", "Dr. Moses Kitheka"));
        dto.setIssuedByDesignation(settingService.getSettingValue("issued_by_designation", "Chief of Party"));

        // Logos
        dto.setLogoMain(getLogoPath("logo_main"));
        dto.setLogoPartner1(getLogoPath("logo_partner1"));
        dto.setLogoPartner2(getLogoPath("logo_partner2"));

        return dto;
    }

    /**
     * Get full file path for logo with null safety
     */
    private String getLogoPath(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }

        String fileName = settingService.getSettingValue(key);
        if (fileName != null && !fileName.isEmpty()) {
            try {
                File file = new File(fileStorageService.getStorageDirectory().toFile(), fileName);
                if (file.exists()) {
                    return file.toURI().toString();
                } else {
                    log.debug("Logo file not found for key {}: {}", key, fileName);
                }
            } catch (Exception e) {
                log.warn("Could not resolve logo path for key: {}", key, e);
            }
        }
        return null;
    }

    /**
     * Render HTML to PDF using Flying Saucer
     */
    private ByteArrayOutputStream renderPdf(String htmlContent) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream;
    }
}