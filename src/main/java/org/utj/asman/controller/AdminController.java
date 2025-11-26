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
        model.addAttribute("newUser", new User());
        return "admin/user_list";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user) {
        try {
            userService.save(user);
        } catch (Exception e) {
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
        model.addAttribute("facilities", assetService.getAllFacilities());
        return "admin/reports";
    }

    @GetMapping("/export/pdf/{facilityId}")
    public ResponseEntity<ByteArrayResource> exportPdf(@PathVariable Long facilityId) {
        try {
            Optional<Facility> facilityOpt = assetService.getAllFacilities().stream()
                    .filter(f -> f.getId().equals(facilityId))
                    .findFirst();

            if (!facilityOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            List<AssetRecord> assets = assetService.getAssetRecords(facilityId);
            byte[] pdfBytes = pdfService.generateAssignmentFormsPdf(facilityOpt.get(), assets).toByteArray();
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment;filename=Assignment_Form_" + facilityOpt.get().getMflCode() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- CPU SPECIFICATIONS ---

    @GetMapping("/cpu-specs")
    public String listCpuSpecs(Model model) {
        model.addAttribute("page", "cpu-specs");
        model.addAttribute("specs", cpuSpecService.getAllCpuSpecs());
        model.addAttribute("newSpec", new CpuSpecification());
        return "admin/cpu_spec_list";
    }

    @PostMapping("/cpu-specs/save")
    @ResponseBody
    public ResponseEntity<?> saveCpuSpec(@ModelAttribute CpuSpecification spec) {
        try {
            CpuSpecification saved = cpuSpecService.saveCpuSpec(spec);
            return ResponseEntity.ok().body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/cpu-specs/get/{id}")
    @ResponseBody
    public ResponseEntity<?> getCpuSpec(@PathVariable Long id) {
        try {
            Optional<CpuSpecification> spec = cpuSpecService.getSpecificationById(id);
            if (spec.isPresent()) {
                return ResponseEntity.ok().body(spec.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/cpu-specs/delete/{id}")
    public String deleteCpuSpec(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cpuSpecService.deleteCpuSpec(id);
            redirectAttributes.addFlashAttribute("message", "Specification deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting specification: " + e.getMessage());
        }
        return "redirect:/admin/cpu-specs";
    }

    // --- ASSET MANAGEMENT ---

    @GetMapping("/assets")
    public String listAssets(@RequestParam(required = false) Long facilityId, Model model) {
        model.addAttribute("assets", assetService.getAssetRecords(facilityId));
        model.addAttribute("facilities", assetService.getAllFacilities());
        return "admin/asset_management";
    }
}