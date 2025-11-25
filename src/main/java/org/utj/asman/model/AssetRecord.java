package org.utj.asman.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "asset_records")
public class AssetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(name = "allocation_date", nullable = false)
    private LocalDateTime allocationDate;

    @Column(name = "cpu_serial", nullable = false, unique = true)
    private String cpuSerial;

    // --- CHANGED: Replaced direct fields with relation ---
    @ManyToOne
    @JoinColumn(name = "cpu_spec_id")
    private CpuSpecification cpuSpecification;
    // -----------------------------------------------------

    @Column(name = "monitor_serial")
    private String monitorSerial;

    @Column(name = "monitor_model")
    private String monitorModel;

    @Column(name = "ups_serial")
    private String upsSerial;

    @Column(name = "ups_model")
    private String upsModel;

    @Column(name = "asset_tag")
    private String assetTag;

    @PrePersist
    protected void onCreate() {
        if (allocationDate == null) {
            allocationDate = LocalDateTime.now();
        }
    }
}