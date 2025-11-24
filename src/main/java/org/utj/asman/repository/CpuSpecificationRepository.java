package org.utj.asman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.utj.asman.model.CpuSpecification;
import java.util.List;

public interface CpuSpecificationRepository extends JpaRepository<CpuSpecification, Long> {
    List<CpuSpecification> findAllByOrderByModelNameAsc();
}