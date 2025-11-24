package org.utj.asman.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "cpu_specifications")
public class CpuSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model_name", nullable = false, unique = true)
    private String modelName; // e.g., "OptiPlex 5090"

    @Column(name = "processor_spec")
    private String processorSpec; // e.g., "Intel Core i5"

    private String memory; // e.g., "8GB"

    @Column(name = "hard_disk_spec")
    private String hardDiskSpec; // e.g., "500GB HDD"
}