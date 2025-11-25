package org.utj.asman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utj.asman.model.County;
import org.utj.asman.repository.CountyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CountyService {

    @Autowired
    private CountyRepository countyRepository;

    public List<County> getAllCounties() {
        return countyRepository.findAll();
    }

    public Optional<County> getCountyById(Long id) {
        return countyRepository.findById(id);
    }

    public County saveCounty(County county) {
        // If county has an ID, it's an update - fetch existing and update it
        if (county.getId() != null) {
            County existing = countyRepository.findById(county.getId())
                    .orElseThrow(() -> new RuntimeException("County not found"));
            existing.setCountyName(county.getCountyName());
            existing.setCountyCode(county.getCountyCode());
            return countyRepository.save(existing);
        }
        // Otherwise it's a new county
        return countyRepository.save(county);
    }

    public void deleteCounty(Long id) {
        countyRepository.deleteById(id);
    }
}
