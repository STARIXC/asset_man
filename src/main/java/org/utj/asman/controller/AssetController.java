package org.utj.asman.controller;

import org.utj.asman.dto.AssetEntryDTO;
import org.utj.asman.model.AssetRecord;
import org.utj.asman.model.CpuSpecification;
import org.utj.asman.model.Facility;
import org.utj.asman.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*") // Allow access from mobile app
public class AssetController {

    @Autowired
    private AssetService assetService;

    // --- CONFIGURATION ENDPOINTS (For App Dropdowns) ---

    /**
     * URL: GET /api/v1/facilities
     * Returns the list of facilities for the "Select Facility" dropdown.
     */
    @GetMapping("/facilities")
    public ResponseEntity<List<Facility>> getFacilities() {
        return ResponseEntity.ok(assetService.getAllFacilities());
    }

    /**
     * URL: GET /api/v1/cpu-specs
     * Returns the list of CPU Specifications for the "CPU Model" dropdown.
     */
    @GetMapping("/cpu-specs")
    public ResponseEntity<List<CpuSpecification>> getCpuSpecs() {
        return ResponseEntity.ok(assetService.getAllCpuSpecs());
    }

    // --- ASSET CRUD ENDPOINTS ---

    /**
     * URL: GET /api/v1/assets?facilityId=1
     * Returns assets, optionally filtered by facility.
     */
    @GetMapping("/assets")
    public ResponseEntity<List<AssetRecord>> getAssets(@RequestParam(required = false) Long facilityId) {
        return ResponseEntity.ok(assetService.getAssetRecords(facilityId));
    }

    /**
     * URL: POST /api/v1/assets
     * Receives scanned data from the Android app.
     */
    @PostMapping("/assets")
    public ResponseEntity<?> createAsset(@RequestBody AssetEntryDTO assetEntryDTO) {
        try {
            AssetRecord savedRecord = assetService.saveAsset(assetEntryDTO);
            return ResponseEntity.ok(savedRecord);
        } catch (IllegalArgumentException e) {
            // Return 400 for Validation Errors (e.g., Duplicate Serial)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Return 500 for server errors
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Server Error: " + e.getMessage());
        }
    }

    /**
     * URL: PUT /api/v1/assets/{id}
     * Updates an existing asset.
     */
    @PutMapping("/assets/{id}")
    public ResponseEntity<?> updateAsset(@PathVariable Long id, @RequestBody AssetEntryDTO dto) {
        System.out.println("Received Update Request for ID: " + id);
        System.out.println("Payload: " + dto.toString()); // Ensure DTO has @ToString (Lombok @Data does this)

        try {
            return ResponseEntity.ok(assetService.updateAsset(id, dto));
        } catch (Exception e) {
            e.printStackTrace(); // Print the full error stack trace to console
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- HELPER ENDPOINTS ---

    /**
     * URL: GET /api/v1/models/suggest?type=monitor&query=dell
     * Auto-complete for Monitor and UPS models.
     */
    @GetMapping("/models/suggest")
    public ResponseEntity<List<String>> suggestModels(
            @RequestParam String type,
            @RequestParam String query) {

        if (query == null || query.length() < 2) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
        return ResponseEntity.ok(assetService.searchModels(type, query));
    }
}