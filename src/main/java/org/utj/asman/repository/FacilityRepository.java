package org.utj.asman.repository;

import org.utj.asman.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Facility entities (e.g., hospitals, health centers).
 * Extends JpaRepository to inherit standard CRUD operations.
 */
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    /**
     * Finds a Facility by its unique MFL code.
     * @param mflCode The unique Master Facility List code.
     * @return An Optional containing the found Facility or empty if not found.
     */
    Optional<Facility> findByMflCode(String mflCode);

    /**
     * Checks if a Facility with the given MFL code already exists.
     * Used for ensuring uniqueness during creation or update.
     * @param mflCode The Master Facility List code to check.
     * @return True if a facility with this MFL code exists, false otherwise.
     */
    boolean existsByMflCode(String mflCode);

    /**
     * Searches for facilities whose name contains the specified search term (case-insensitive).
     * This is useful for lookup/auto-complete functionality in the user interface.
     * @param searchTerm The partial name to search for.
     * @return A list of matching Facilities.
     */
    List<Facility> findByFacilityNameContainingIgnoreCase(String searchTerm);

    /**
     * Searches for facilities based on a partial facility name or MFL code (for quick search).
     * @param query The search term.
     * @return A list of facilities matching the query.
     */
    @Query("SELECT f FROM Facility f WHERE LOWER(f.facilityName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(f.mflCode) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Facility> searchFacilities(@Param("query") String query);
}