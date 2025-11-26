package org.utj.asman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.utj.asman.model.Facility;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.utj.asman.dto.FacilityPdfDto;
import org.utj.asman.util.PdfGenerator;
import org.utj.asman.service.CountyService;
import org.utj.asman.service.FacilityService;

@Controller
@RequestMapping("/admin/facilities")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private SpringTemplateEngine templateEngine;

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

    @GetMapping("/pdf/{id}")

    public ResponseEntity<byte[]> generateFacilityPdf(@PathVariable Long id) throws Exception {
        Facility facility = facilityService.getFacilityById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        // Convert to DTO for the PDF view
        FacilityPdfDto dto = FacilityPdfDto.from(facility);
        // Prepare Thymeleaf context
        Context ctx = new Context();
        ctx.setVariable("facility", dto);
        String html = templateEngine.process("admin/facility_pdf", ctx);
        byte[] pdfBytes = PdfGenerator.generatePdfFromHtml(html);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "facility_" + id + ".pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
