package org.utj.asman.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.utj.asman.model.AssetRecord;
import org.utj.asman.model.CpuSpecification;
import org.utj.asman.model.Facility;
import org.utj.asman.model.User;
import org.utj.asman.service.AssetService;
import org.utj.asman.service.CpuSpecificationService;
import org.utj.asman.service.PdfService;
import org.utj.asman.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private PdfService pdfService;
    @Autowired
    private CpuSpecificationService cpuSpecService;

    // --- DASHBOARD ---

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("page", "dashboard");

        // Calculate stats for the dashboard cards
        long userCount = userService.findAll().size();
        long facilityCount = assetService.getAllFacilities().size();
        long assetCount = assetService.getAssetRecords(null).size();

        model.addAttribute("userCount", userCount);
        model.addAttribute("facilityCount", facilityCount);
        model.addAttribute("assetCount", assetCount);

        return "admin/dashboard";
    }

    // --- USER MANAGEMENT ---

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("page", "users");
        model.addAttribute("users", userService.findAll());
        // Add empty user object for the "Create User" form
        model.addAttribute("newUser", new User());
        return "admin/user_list";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user) {
        try {
            userService.save(user);
        } catch (Exception e) {
            // In a real app, add error to FlashAttributes
            System.err.println("Error saving user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    // --- REPORTS & EXPORT ---

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("page", "reports");
        // Load facilities for the dropdown
        model.addAttribute("facilities", assetService.getAllFacilities());
        return "admin/reports";
    }

    @GetMapping("/export/pdf/{facilityId}")
    public ResponseEntity<ByteArrayResource> exportPdf(@PathVariable Long facilityId) {
        try {
            // 1. Fetch Facility
            Optional<Facility> facilityOpt = assetService.getAllFacilities().stream()
                    .filter(f -> f.getId().equals(facilityId))
                    .findFirst();

            if (!facilityOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            // 2. Fetch Assets for that facility
            List<AssetRecord> assets = assetService.getAssetRecords(facilityId);

            // 3. Generate PDF bytes
            byte[] pdfBytes = pdfService.generateAssignmentFormsPdf(facilityOpt.get(), assets).toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            // 4. Return as Download
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Assignment_Form_" + facilityOpt.get().getMflCode() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/cpu-specs")
    public String listCpuSpecs(Model model) {
        model.addAttribute("page", "cpu-specs");
        model.addAttribute("specs", cpuSpecService.getAllCpuSpecs());
        // Empty object for the modal form
        model.addAttribute("newSpec", new CpuSpecification());
        return "admin/cpu_spec_list";
    }

    @PostMapping("/cpu-specs/save")
    public String saveCpuSpec(@ModelAttribute CpuSpecification spec, RedirectAttributes redirectAttributes) {
        try {
            // Using our service that handles normalization/uniqueness logic
            // Note: If you want to FORCE creation even if exists (rare), use save() directly.
            // But generally, findOrCreate or saveCpuSpec with validation is better.
            cpuSpecService.saveCpuSpec(spec);
            redirectAttributes.addFlashAttribute("message", "Specification saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving specification: " + e.getMessage());
        }
        return "redirect:/admin/cpu-specs";
    }
    @GetMapping("/assets")
    public String listAssets(@RequestParam(required = false) Long facilityId, Model model) {
        model.addAttribute("assets", assetService.getAssetRecords(facilityId));
        model.addAttribute("facilities", assetService.getAllFacilities());
        return "admin/asset_management";
    }

}