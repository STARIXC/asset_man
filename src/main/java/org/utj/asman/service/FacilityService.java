package org.utj.asman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utj.asman.model.Facility;
import org.utj.asman.repository.FacilityRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FacilityService {

    @Autowired
    private FacilityRepository facilityRepository;

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public Optional<Facility> getFacilityById(Long id) {
        return facilityRepository.findById(id);
    }

    public Facility saveFacility(Facility facility) {
        // If facility has an ID, it's an update - fetch existing and update it
        if (facility.getId() != null) {
            Facility existing = facilityRepository.findById(facility.getId())
                    .orElseThrow(() -> new RuntimeException("Facility not found"));
            existing.setFacilityName(facility.getFacilityName());
            existing.setMflCode(facility.getMflCode());
            existing.setCounty(facility.getCounty());
            return facilityRepository.save(existing);
        }
        // Otherwise it's a new facility
        return facilityRepository.save(facility);
    }

    public void deleteFacility(Long id) {
        facilityRepository.deleteById(id);
    }

    public Facility updateFacility(Long id, Facility facilityDetails) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        facility.setFacilityName(facilityDetails.getFacilityName());
        facility.setMflCode(facilityDetails.getMflCode());
        facility.setCounty(facilityDetails.getCounty());
        
        return facilityRepository.save(facility);
    }
}