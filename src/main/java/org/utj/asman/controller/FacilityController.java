package org.utj.asman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.utj.asman.dto.FacilityDto;
import org.utj.asman.dto.FacilityResponseDto;
import org.utj.asman.service.FacilityService;
import org.utj.asman.service.CountyService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/facilities")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private CountyService countyService;

    /**
     * Display facility list page
     */
    @GetMapping
    public String listFacilities(Model model) {
        // Load facilities as DTOs with null-safe handling
        List<FacilityResponseDto> facilities = facilityService.getAllFacilitiesDto();
        
        model.addAttribute("facilities", facilities);
        model.addAttribute("counties", countyService.getAllCounties());
        model.addAttribute("newFacility", new FacilityDto());
        
        return "admin/facility_list";
    }

    /**
     * Save or update facility
     */
    @PostMapping("/save")
    public String saveFacility(
            @Valid @ModelAttribute("newFacility") FacilityDto facilityDto,
            BindingResult result,
            @RequestParam(value = "county.id", required = false) Long countyId,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("facilities", facilityService.getAllFacilitiesDto());
            model.addAttribute("counties", countyService.getAllCounties());
            return "admin/facility_list";
        }

        try {
            // Save facility - no need to store return value if not used
            facilityService.saveFacility(facilityDto, countyId);
            
            String message = facilityDto.getId() != null 
                ? "Facility updated successfully!" 
                : "Facility added successfully!";
            
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving facility: " + e.getMessage());
        }

        return "redirect:/admin/facilities";
    }

    /**
     * Get facility by ID as JSON (for edit functionality)
     */
    @GetMapping("/get/{id}")
    @ResponseBody
    public ResponseEntity<FacilityResponseDto> getFacility(@PathVariable Long id) {
        return facilityService.getFacilityDtoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete facility
     */
    @PostMapping("/delete/{id}")
    public String deleteFacility(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            facilityService.deleteFacility(id);
            redirectAttributes.addFlashAttribute("successMessage", "Facility deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting facility: " + e.getMessage());
        }
        return "redirect:/admin/facilities";
    }
}