package org.utj.asman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.utj.asman.model.Facility;
import org.utj.asman.service.CountyService;
import org.utj.asman.service.FacilityService;

@Controller
@RequestMapping("/admin/facilities")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private CountyService countyService;

    @GetMapping
    public String listFacilities(Model model) {
        model.addAttribute("facilities", facilityService.getAllFacilities());
        model.addAttribute("counties", countyService.getAllCounties());
        model.addAttribute("newFacility", new Facility());
        return "admin/facility_list";
    }

    @PostMapping("/save")
    public String saveFacility(@ModelAttribute Facility facility) {
        facilityService.saveFacility(facility);
        return "redirect:/admin/facilities";
    }

    @PostMapping("/delete/{id}")
    public String deleteFacility(@PathVariable Long id) {
        facilityService.deleteFacility(id);
        return "redirect:/admin/facilities";
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public Facility getFacility(@PathVariable Long id) {
        return facilityService.getFacilityById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public Facility updateFacility(@PathVariable Long id, @RequestBody Facility facility) {
        return facilityService.updateFacility(id, facility);
    }
}
