package org.utj.asman.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.utj.asman.model.Setting;
import org.utj.asman.service.SettingService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/settings")
public class SettingsController {

    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);

    @Autowired
    private SettingService settingService;

    @GetMapping
    public String showSettings(Model model) {
        try {
            List<Setting> settings = settingService.getAllSettings();
            model.addAttribute("settings", settings);
            return "admin/settings";
        } catch (Exception e) {
            log.error("Error loading settings", e);
            model.addAttribute("error", "Failed to load settings: " + e.getMessage());
            return "admin/settings";
        }
    }

    @PostMapping("/save")
    public String saveSettings(@RequestParam Map<String, String> allParams,
            @RequestParam(value = "logo_main", required = false) MultipartFile logoMain,
            @RequestParam(value = "logo_partner1", required = false) MultipartFile logoPartner1,
            @RequestParam(value = "logo_partner2", required = false) MultipartFile logoPartner2,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Handle text settings
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                String key = entry.getKey();
                
                // Skip CSRF token and file upload parameter names
                if (key.equals("_csrf") || key.startsWith("logo_")) {
                    continue;
                }
                
                try {
                    // Get existing setting to preserve label and type
                    Setting existing = settingService.getSettingByKey(key);
                    String label = existing != null ? existing.getLabel() : key;
                    String type = existing != null ? existing.getType() : "text";
                    
                    settingService.saveSetting(key, entry.getValue(), label, type);
                    log.debug("Saved setting: {} = {}", key, entry.getValue());
                    
                } catch (Exception e) {
                    log.error("Error saving setting: {}", key, e);
                    redirectAttributes.addFlashAttribute("error", 
                        "Failed to save setting '" + key + "': " + e.getMessage());
                    return "redirect:/admin/settings";
                }
            }

            // Handle file uploads with proper validation
            if (logoMain != null && !logoMain.isEmpty()) {
                try {
                    validateImageFile(logoMain);
                    settingService.saveLogo("logo_main", logoMain, "Main Logo (Left)");
                    log.info("Saved logo_main: {}", logoMain.getOriginalFilename());
                } catch (Exception e) {
                    log.error("Error saving logo_main", e);
                    redirectAttributes.addFlashAttribute("error", 
                        "Failed to upload main logo: " + e.getMessage());
                    return "redirect:/admin/settings";
                }
            }
            
            if (logoPartner1 != null && !logoPartner1.isEmpty()) {
                try {
                    validateImageFile(logoPartner1);
                    settingService.saveLogo("logo_partner1", logoPartner1, "Partner Logo 1 (Center)");
                    log.info("Saved logo_partner1: {}", logoPartner1.getOriginalFilename());
                } catch (Exception e) {
                    log.error("Error saving logo_partner1", e);
                    redirectAttributes.addFlashAttribute("error", 
                        "Failed to upload partner logo 1: " + e.getMessage());
                    return "redirect:/admin/settings";
                }
            }
            
            if (logoPartner2 != null && !logoPartner2.isEmpty()) {
                try {
                    validateImageFile(logoPartner2);
                    settingService.saveLogo("logo_partner2", logoPartner2, "Partner Logo 2 (Right)");
                    log.info("Saved logo_partner2: {}", logoPartner2.getOriginalFilename());
                } catch (Exception e) {
                    log.error("Error saving logo_partner2", e);
                    redirectAttributes.addFlashAttribute("error", 
                        "Failed to upload partner logo 2: " + e.getMessage());
                    return "redirect:/admin/settings";
                }
            }

            redirectAttributes.addFlashAttribute("success", "Settings updated successfully!");
            
        } catch (Exception e) {
            log.error("Unexpected error saving settings", e);
            redirectAttributes.addFlashAttribute("error", 
                "An unexpected error occurred: " + e.getMessage());
        }

        return "redirect:/admin/settings";
    }

    @PostMapping("/add")
    public String addSetting(@RequestParam("key") String key,
            @RequestParam("value") String value,
            @RequestParam("label") String label,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validate input
            if (key == null || key.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Setting key cannot be empty");
                return "redirect:/admin/settings";
            }
            
            // Check if setting already exists
            Setting existing = settingService.getSettingByKey(key);
            if (existing != null) {
                redirectAttributes.addFlashAttribute("error", 
                    "Setting with key '" + key + "' already exists");
                return "redirect:/admin/settings";
            }
            
            settingService.saveSetting(key, value, label, "text");
            log.info("Added new setting: {}", key);
            redirectAttributes.addFlashAttribute("success", "New setting added successfully!");
            
        } catch (Exception e) {
            log.error("Error adding new setting", e);
            redirectAttributes.addFlashAttribute("error", 
                "Failed to add setting: " + e.getMessage());
        }
        
        return "redirect:/admin/settings";
    }
    
    /**
     * Validates that the uploaded file is a valid image
     */
    private void validateImageFile(MultipartFile file) throws IOException {
        // Check file size (e.g., max 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new IOException("File size exceeds maximum allowed size of 5MB");
        }
        
        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("File must be an image (jpg, png, gif, etc.)");
        }
        
        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches(".*\\.(jpg|jpeg|png|gif|svg|webp)$")) {
            throw new IOException("Invalid file extension. Allowed: jpg, jpeg, png, gif, svg, webp");
        }
    }
}