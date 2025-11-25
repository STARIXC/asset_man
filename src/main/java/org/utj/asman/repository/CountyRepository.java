package org.utj.asman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.utj.asman.model.County;

import java.util.Optional;

@Repository
public interface CountyRepository extends JpaRepository<County, Long> {
    Optional<County> findByCountyCode(String countyCode);
}
