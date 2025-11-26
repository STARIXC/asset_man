package org.utj.asman.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

/**
 * Entity to store distinct, reusable hardware specifications for CPUs.
 * This helps in normalizing data and easily creating lookup lists.
 */
@Entity
@Data
@Table(name = "cpu_specifications", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "processor", "memory", "hard_disk" })
})
public class CpuSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fields taken from the "COMPUTER DETAILS AND SPECS" section of the assignment
    // form
    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "processor", nullable = false)
    private String processor; // e.g., "Intel® Core™ i5 10Th Gen"

    @Column(name = "memory", nullable = false)
    private String memory; // e.g., "8.00 GB"

    @Column(name = "hard_disk", nullable = false)
    private String hardDisk; // e.g., "1000 GB"

    @Column(name = "purchase_date", nullable = true)
    private LocalDate purchaseDate;

    @Column(name = "supplier", nullable = true)
    private String supplier;
}