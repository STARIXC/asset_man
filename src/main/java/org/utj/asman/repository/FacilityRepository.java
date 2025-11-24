package org.utj.asman.repository;


import org.utj.asman.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    // We might need to find a facility by its MFL code later
    Optional<Facility> findByMflCode(String mflCode);
}