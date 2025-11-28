package org.utj.asman.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.utj.asman.model.Setting;
import org.utj.asman.repository.SettingRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingService {

    private static final Logger log = LoggerFactory.getLogger(SettingService.class);

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Initialize default settings on application startup
     */
    @PostConstruct
    public void init() {
        initDefaultSettings();
    }

    /**
     * Get all settings from database
     */
    public List<Setting> getAllSettings() {
        try {
            return settingRepository.findAll();
        } catch (Exception e) {
            log.error("Error retrieving all settings", e);
            throw new RuntimeException("Failed to retrieve settings", e);
        }
    }

    /**
     * Get a single setting by its key
     */
    public Setting getSettingByKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            log.warn("Attempted to get setting with null or empty key");
            return null;
        }
        
        try {
            Optional<Setting> setting = settingRepository.findBySettingKey(key);
            return setting.isPresent() ? setting.get() : null;
        } catch (Exception e) {
            log.error("Error retrieving setting with key: {}", key, e);
            return null;
        }
    }

    /**
     * Get the value of a setting by its key
     */
    public String getSettingValue(String key) {
        Setting setting = getSettingByKey(key);
        return setting != null ? setting.getValue() : null;
    }

    /**
     * Get the value of a setting with a default fallback
     */
    public String getSettingValue(String key, String defaultValue) {
        String value = getSettingValue(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Save or update a setting
     */
    @Transactional
    public void saveSetting(String key, String value, String label, String type) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Setting key cannot be null or empty");
        }
        
        try {
            Optional<Setting> optionalSetting = settingRepository.findBySettingKey(key);
            Setting setting;
            
            if (optionalSetting.isPresent()) {
                setting = optionalSetting.get();
            } else {
                setting = new Setting();
            }
            
            setting.setSettingKey(key);
            setting.setValue(value);
            setting.setLabel(label != null ? label : key);
            setting.setType(type != null ? type : "text");
            
            settingRepository.save(setting);
            log.info("Saved setting: {} = {}", key, value);
            
        } catch (Exception e) {
            log.error("Error saving setting: {}", key, e);
            throw new RuntimeException("Failed to save setting: " + key, e);
        }
    }

    /**
     * Save a logo file and update the setting
     */
    @Transactional
    public void saveLogo(String key, MultipartFile file, String label) throws IOException {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Logo key cannot be null or empty");
        }
        
        if (file == null || file.isEmpty()) {
            log.warn("Attempted to save logo with null or empty file for key: {}", key);
            return;
        }
        
        try {
            // Delete old logo file if exists
            Setting existingSetting = getSettingByKey(key);
            if (existingSetting != null && existingSetting.getValue() != null) {
                try {
                    fileStorageService.deleteFile(existingSetting.getValue());
                    log.info("Deleted old logo file: {}", existingSetting.getValue());
                } catch (Exception e) {
                    log.warn("Could not delete old logo file: {}", existingSetting.getValue(), e);
                    // Continue anyway - not critical
                }
            }
            
            // Store new file
            String fileName = fileStorageService.storeFile(file);
            log.info("Stored new logo file: {} for key: {}", fileName, key);
            
            // Save setting with new filename
            saveSetting(key, fileName, label, "image");
            
        } catch (IOException e) {
            log.error("Error saving logo for key: {}", key, e);
            throw new IOException("Failed to save logo: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a setting by key
     */
    @Transactional
    public boolean deleteSetting(String key) {
        if (key == null || key.trim().isEmpty()) {
            log.warn("Attempted to delete setting with null or empty key");
            return false;
        }
        
        try {
            Optional<Setting> optionalSetting = settingRepository.findBySettingKey(key);
            if (optionalSetting.isPresent()) {
                Setting setting = optionalSetting.get();
                
                // If it's an image setting, delete the file too
                if ("image".equals(setting.getType()) && setting.getValue() != null) {
                    try {
                        fileStorageService.deleteFile(setting.getValue());
                    } catch (Exception e) {
                        log.warn("Could not delete file for setting: {}", key, e);
                    }
                }
                
                settingRepository.delete(setting);
                log.info("Deleted setting: {}", key);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error deleting setting: {}", key, e);
            return false;
        }
    }

    /**
     * Get all settings as a Map for easy access in templates/PDF generation
     */
    public Map<String, String> getSettingsMap() {
        try {
            List<Setting> settings = settingRepository.findAll();
            Map<String, String> settingsMap = new HashMap<String, String>();
            for (Setting s : settings) {
                if (s.getSettingKey() != null) {
                    settingsMap.put(s.getSettingKey(), s.getValue());
                }
            }
            return settingsMap;
        } catch (Exception e) {
            log.error("Error creating settings map", e);
            return new HashMap<String, String>(); // Return empty map instead of failing
        }
    }

    /**
     * Get the URL for the upload directory
     */
    public String getUploadDirectoryUrl() {
        try {
            return fileStorageService.getStorageDirectory().toUri().toString();
        } catch (Exception e) {
            log.error("Error getting upload directory URL", e);
            return "";
        }
    }

    /**
     * Initialize default settings if they don't exist
     */
    @Transactional
    public void initDefaultSettings() {
        try {
            long count = settingRepository.count();
            
            if (count == 0) {
                log.info("Initializing default settings...");
                
                // Organization settings
                saveSetting("org_name", "USAID Tujenge Jamii", "Organization Name", "text");
                saveSetting("org_address", "P.O. Box 12345, Nairobi", "Address", "text");
                saveSetting("org_email", "info@tujengejamii.org", "Email", "email");
                saveSetting("org_phone", "+254 700 000 000", "Phone", "text");
                saveSetting("org_website", "https://tujengejamii.org", "Website", "url");
                
                // Logo placeholders (will be uploaded later)
                saveSetting("logo_main", "", "Main Logo (Left)", "image");
                saveSetting("logo_partner1", "", "Partner Logo 1 (Center)", "image");
                saveSetting("logo_partner2", "", "Partner Logo 2 (Right)", "image");
                
                // Report settings
                saveSetting("report_footer", "© 2024 USAID Tujenge Jamii. All rights reserved.", 
                           "Report Footer", "textarea");
                 // 
                saveSetting("issued_by_name", "Evans Limo", "Issued By Name", "text");
                saveSetting("issued_by_designation", "I.T Officer", "Issued By Designation", "text");
                saveSetting("chief_of_party_name", "Dr. Moses Kitheka", "Chief of Party Name", "text");
                saveSetting("chief_of_party_title", "Chief of Party", "Chief of Party Title", "text");
                
                log.info("Default settings initialized successfully");
            } else {
                log.debug("Settings already exist (count: {}), skipping initialization", count);
            }
            
        } catch (Exception e) {
            log.error("Could not initialize default settings", e);
            // Don't throw exception - allow application to start even if settings fail
        }
    }

    /**
     * Check if a setting exists
     */
    public boolean settingExists(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        try {
            Optional<Setting> setting = settingRepository.findBySettingKey(key);
            return setting.isPresent();
        } catch (Exception e) {
            log.error("Error checking if setting exists: {}", key, e);
            return false;
        }
    }

    /**
     * Get settings by type
     */
    public List<Setting> getSettingsByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return new ArrayList<Setting>();
        }
        
        try {
            return settingRepository.findByType(type);
        } catch (Exception e) {
            log.error("Error retrieving settings by type: {}", type, e);
            return new ArrayList<Setting>();
        }
    }
}