package org.utj.asman.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.utj.asman.dto.FacilityResponseDto;
import org.utj.asman.model.AssetRecord;
import org.utj.asman.repository.AssetRecordRepository;
import org.utj.asman.service.EnhancedPdfService;
import org.utj.asman.service.FacilityService;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
@RequestMapping("/admin/pdf")
public class PdfController {

    private static final Logger log = LoggerFactory.getLogger(PdfController.class);

    @Autowired
    private EnhancedPdfService pdfService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private AssetRecordRepository assetRecordRepository;

    /**
     * Generate Receipt PDF for a facility
     * GET /admin/pdf/receipt/{facilityId}
     * 
     * FIXED: Now uses facilityId directly to avoid lazy initialization issues
     */
    @GetMapping("/receipt/{facilityId}")
    public ResponseEntity<byte[]> generateReceipt(@PathVariable Long facilityId) {
        try {
            log.info("Generating receipt PDF for facility: {}", facilityId);
            
            // Validate facility exists using DTO service
            FacilityResponseDto facility = facilityService.getFacilityDtoById(facilityId)
                    .orElseThrow(() -> new RuntimeException("Facility not found with ID: " + facilityId));
            
            // Get assets for this facility
            List<AssetRecord> assets = assetRecordRepository.findByFacilityId(facilityId);
            
            if (assets.isEmpty()) {
                log.warn("No assets found for facility: {}", facilityId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No assets found for this facility".getBytes());
            }
            
            // Generate PDF using facilityId (not entity) - uses DTO internally
            ByteArrayOutputStream pdfStream = pdfService.generateReceiptPdf(facilityId, assets);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                    "receipt_" + facility.getMflCode() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            log.info("Successfully generated receipt PDF for facility: {}", facilityId);
            return new ResponseEntity<byte[]>(pdfStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error generating receipt PDF for facility: {}", facilityId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Generate Assignment Form PDF for a single asset
     * GET /admin/pdf/assignment/{assetId}
     */
    @GetMapping("/assignment/{assetId}")
    public ResponseEntity<byte[]> generateAssignmentForm(@PathVariable Long assetId) {
        try {
            log.info("Generating assignment form PDF for asset: {}", assetId);
            
            AssetRecord asset = assetRecordRepository.findById(assetId)
                    .orElseThrow(() -> new RuntimeException("Asset not found with ID: " + assetId));
            
            ByteArrayOutputStream pdfStream = pdfService.generateAssignmentFormPdf(asset);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = asset.getCpuSerial() != null && !asset.getCpuSerial().isEmpty()
                    ? "assignment_" + asset.getCpuSerial() + ".pdf"
                    : "assignment_" + assetId + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            log.info("Successfully generated assignment form PDF for asset: {}", assetId);
            return new ResponseEntity<byte[]>(pdfStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error generating assignment form PDF for asset: {}", assetId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Generate bulk assignment forms for a facility
     * GET /admin/pdf/assignment/facility/{facilityId}
     */
    @GetMapping("/assignment/facility/{facilityId}")
    public ResponseEntity<byte[]> generateBulkAssignmentForms(@PathVariable Long facilityId) {
        try {
            log.info("Generating bulk assignment forms for facility: {}", facilityId);
            
            // Validate facility exists using DTO service
            FacilityResponseDto facility = facilityService.getFacilityDtoById(facilityId)
                    .orElseThrow(() -> new RuntimeException("Facility not found with ID: " + facilityId));
            
            List<AssetRecord> assets = assetRecordRepository.findByFacilityId(facilityId);
            
            if (assets.isEmpty()) {
                log.warn("No assets found for facility: {}", facilityId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No assets found for this facility".getBytes());
            }
            
            ByteArrayOutputStream pdfStream = pdfService.generateBulkAssignmentForms(assets);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                    "assignment_forms_" + facility.getMflCode() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            log.info("Successfully generated bulk assignment forms for facility: {}", facilityId);
            return new ResponseEntity<byte[]>(pdfStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error generating bulk assignment forms for facility: {}", facilityId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Preview receipt in browser (inline display)
     * GET /admin/pdf/receipt/{facilityId}/preview
     * 
     * FIXED: Now uses facilityId directly to avoid lazy initialization issues
     */
    @GetMapping("/receipt/{facilityId}/preview")
    public ResponseEntity<byte[]> previewReceipt(@PathVariable Long facilityId) {
        try {
            log.info("Generating receipt preview for facility: {}", facilityId);
            
            // Validate facility exists using DTO service (throws exception if not found)
            facilityService.getFacilityDtoById(facilityId)
                    .orElseThrow(() -> new RuntimeException("Facility not found with ID: " + facilityId));
            
            List<AssetRecord> assets = assetRecordRepository.findByFacilityId(facilityId);
            
            if (assets.isEmpty()) {
                log.warn("No assets found for facility: {}", facilityId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No assets found for this facility".getBytes());
            }
            
            // Generate PDF using facilityId (not entity) - uses DTO internally
            ByteArrayOutputStream pdfStream = pdfService.generateReceiptPdf(facilityId, assets);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=receipt_preview.pdf");
            
            log.info("Successfully generated receipt preview for facility: {}", facilityId);
            return new ResponseEntity<byte[]>(pdfStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error previewing receipt PDF for facility: {}", facilityId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Preview assignment form in browser (inline display)
     * GET /admin/pdf/assignment/{assetId}/preview
     */
    @GetMapping("/assignment/{assetId}/preview")
    public ResponseEntity<byte[]> previewAssignmentForm(@PathVariable Long assetId) {
        try {
            log.info("Generating assignment form preview for asset: {}", assetId);
            
            AssetRecord asset = assetRecordRepository.findById(assetId)
                    .orElseThrow(() -> new RuntimeException("Asset not found with ID: " + assetId));
            
            ByteArrayOutputStream pdfStream = pdfService.generateAssignmentFormPdf(asset);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=assignment_preview.pdf");
            
            log.info("Successfully generated assignment form preview for asset: {}", assetId);
            return new ResponseEntity<byte[]>(pdfStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error previewing assignment form PDF for asset: {}", assetId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }
}