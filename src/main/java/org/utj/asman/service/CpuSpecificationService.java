package org.utj.asman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.utj.asman.model.CpuSpecification;
import org.utj.asman.repository.CpuSpecificationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CpuSpecificationService {
    @Autowired
    private final CpuSpecificationRepository cpuSpecificationRepository;

    public CpuSpecificationService(CpuSpecificationRepository cpuSpecificationRepository) {
        this.cpuSpecificationRepository = cpuSpecificationRepository;
    }

    public List<CpuSpecification> getAllCpuSpecs() {
        return cpuSpecificationRepository.findAll();
    }

    public Optional<CpuSpecification> getSpecificationById(Long id) {
        return cpuSpecificationRepository.findById(id);
    }

    /**
     * key logic: Checks for existing spec based on the unique constraint
     * (processor + memory + hardDisk). If found, returns it.
     * If not, saves a new one using ALL provided fields.
     */
    public CpuSpecification findOrCreateSpecification(String manufacturer, String model, String processor,
            String memory, String hardDisk) {
        Optional<CpuSpecification> existingSpec = cpuSpecificationRepository.findByProcessorAndMemoryAndHardDisk(
                processor,
                memory,
                hardDisk);

        if (existingSpec.isPresent()) {
            return existingSpec.get();
        } else {
            CpuSpecification newSpec = new CpuSpecification();
            newSpec.setManufacturer(manufacturer);
            newSpec.setModel(model);
            newSpec.setProcessor(processor);
            newSpec.setMemory(memory);
            newSpec.setHardDisk(hardDisk);
            return cpuSpecificationRepository.save(newSpec);
        }
    }

    public CpuSpecification saveCpuSpec(CpuSpecification spec) {
        return cpuSpecificationRepository.save(spec);
    }

    public void deleteCpuSpec(Long id) {
        cpuSpecificationRepository.deleteById(id);
    }
}