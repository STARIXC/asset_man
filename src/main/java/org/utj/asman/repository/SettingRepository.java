package org.utj.asman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.utj.asman.model.Setting;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    
    // Changed from findByKey to findBySettingKey
    Optional<Setting> findBySettingKey(String settingKey);
    
    // Find settings by type
    List<Setting> findByType(String type);
    
    // Check if setting exists
    boolean existsBySettingKey(String settingKey);
}