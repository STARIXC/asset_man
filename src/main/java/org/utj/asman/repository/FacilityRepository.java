package org.utj.asman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.utj.asman.model.Facility;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    /**
     * Find facility by MFL code
     * Used for validation to ensure MFL codes are unique
     */
    Optional<Facility> findByMflCode(String mflCode);

    /**
     * Find all facilities by county ID
     * Returns empty list if no facilities found
     */
    List<Facility> findByCountyId(Long countyId);

    /**
     * Check if a facility exists with the given MFL code
     */
    boolean existsByMflCode(String mflCode);

    /**
     * Count facilities by county ID
     */
    long countByCountyId(Long countyId);

    /**
     * Find facilities by facility name containing search term (case insensitive)
     */
    List<Facility> findByFacilityNameContainingIgnoreCase(String facilityName);

    /**
     * Find facilities by MFL code containing search term (case insensitive)
     */
    List<Facility> findByMflCodeContainingIgnoreCase(String mflCode);
}