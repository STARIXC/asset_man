package org.utj.asman.controller;

import org.utj.asman.dto.AssetEntryDTO;
import org.utj.asman.model.AssetRecord;
import org.utj.asman.model.Facility;
import org.utj.asman.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*") // Allows access from mobile/emulators without CORS issues
public class AssetController {

    @Autowired
    private AssetService assetService;

    // Endpoint 1: Get list of hospitals for the dropdown
    // URL: GET http://YOUR_IP:8080/api/v1/facilities
    @GetMapping("/facilities")
    public ResponseEntity<List<Facility>> getFacilities() {
        List<Facility> facilities = assetService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }

    // Endpoint 2: Receive scanned data from the Android app
    // URL: POST http://YOUR_IP:8080/api/v1/assets
    @PostMapping("/assets")
    public ResponseEntity<?> createAssetRecord(@RequestBody AssetEntryDTO assetEntryDTO) {
        try {
            AssetRecord savedRecord = assetService.saveAsset(assetEntryDTO);
            return ResponseEntity.ok(savedRecord);
        } catch (RuntimeException e) {
            // Return a 400 Bad Request with the error message (e.g., "Duplicate Serial")
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

  
   @GetMapping("/assets")
   public ResponseEntity<List<AssetRecord>> getAssets(
           @RequestParam(required = false) Long facilityId) {
       
       List<AssetRecord> records = assetService.getAssetRecords(facilityId);
       return ResponseEntity.ok(records);
   }


   // Update Asset
    // URL: PUT /api/v1/assets/{id}
    @PutMapping("/assets/{id}")
    public ResponseEntity<?> updateAsset(@PathVariable Long id, @RequestBody AssetEntryDTO assetEntryDTO) {
        try {
            AssetRecord updatedRecord = assetService.updateAsset(id, assetEntryDTO);
            return ResponseEntity.ok(updatedRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // NEW Endpoint: Auto-Suggest Models
    // URL: GET /api/v1/models/suggest?type=cpu&query=del
    @GetMapping("/models/suggest")
    public ResponseEntity<List<String>> suggestModels(
            @RequestParam String type,
            @RequestParam String query) {
        // Performance: Only search if at least 2-3 chars (optional check)
        if (query.length() < 2) return ResponseEntity.ok(java.util.Collections.emptyList());
        
        return ResponseEntity.ok(assetService.searchModels(type, query));
    }
}