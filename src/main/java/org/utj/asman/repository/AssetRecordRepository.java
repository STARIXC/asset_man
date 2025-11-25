package org.utj.asman.repository;

import org.utj.asman.model.AssetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Repository interface for managing AssetRecord entities (individual computer allocations).
 * Extends JpaRepository to inherit standard CRUD operations.
 */
public interface AssetRecordRepository extends JpaRepository<AssetRecord, Long> {

    /**
     * Finds all AssetRecords assigned to a specific Facility ID.
     * @param facilityId The ID of the facility.
     * @return A list of AssetRecords currently allocated to that facility.
     */
    List<AssetRecord> findByFacilityId(Long facilityId);

    // --- Existence Checks (Validation) ---

    /**
     * Checks if an asset with the given CPU serial number already exists.
     * @param cpuSerial The unique serial number of the CPU.
     * @return True if exists, false otherwise.
     */
    boolean existsByCpuSerial(String cpuSerial);

    /**
     * Checks if an asset with the given Monitor serial number already exists.
     * @param monitorSerial The serial number of the monitor.
     * @return True if exists, false otherwise.
     */
    boolean existsByMonitorSerial(String monitorSerial);

    /**
     * Checks if an asset with the given UPS serial number already exists.
     * @param upsSerial The serial number of the UPS.
     * @return True if exists, false otherwise.
     */
    boolean existsByUpsSerial(String upsSerial);

    // --- Search / Auto-Complete Queries ---

    /**
     * Searches for distinct Monitor Models based on a partial query.
     * Used for auto-complete in the frontend.
     * @param query The partial model name.
     * @return A list of matching monitor models.
     */
    @Query("SELECT DISTINCT a.monitorModel FROM AssetRecord a WHERE a.monitorModel LIKE :query%")
    List<String> searchMonitorModels(@Param("query") String query);

    /**
     * Searches for distinct UPS Models based on a partial query.
     * Used for auto-complete in the frontend.
     * @param query The partial model name.
     * @return A list of matching UPS models.
     */
    @Query("SELECT DISTINCT a.upsModel FROM AssetRecord a WHERE a.upsModel LIKE :query%")
    List<String> searchUpsModels(@Param("query") String query);

    /**
     * Optional: Global search for the Admin Panel.
     * Search by CPU Serial, Facility Name, or CPU Model Name (via the joined relationship).
     * @param query The search term.
     * @return List of matching assets.
     */
    @Query("SELECT a FROM AssetRecord a " +
            "JOIN a.facility f " +
            "JOIN a.cpuSpecification cs " +
            "WHERE LOWER(a.cpuSerial) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(f.facilityName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(cs.model) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<AssetRecord> searchAssets(@Param("query") String query);


    // Check if serial exists for ANY record EXCEPT the one with this ID
    boolean existsByCpuSerialAndIdNot(String cpuSerial, Long id);

    boolean existsByMonitorSerialAndIdNot(String monitorSerial, Long id);

    boolean existsByUpsSerialAndIdNot(String upsSerial, Long id);
}