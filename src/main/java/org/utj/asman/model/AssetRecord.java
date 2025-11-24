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

    // Relationship: Many Assets belong to One Facility
    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(name = "allocation_date", nullable = false)
    private LocalDateTime allocationDate;

    // CPU Details
    @Column(name = "cpu_serial", nullable = false, unique = true)
    private String cpuSerial;

    @Column(name = "cpu_model")
    private String cpuModel;

    // Monitor Details
    @Column(name = "monitor_serial")
    private String monitorSerial;

    @Column(name = "monitor_model")
    private String monitorModel;

    // UPS Details
    @Column(name = "ups_serial")
    private String upsSerial;

    @Column(name = "ups_model")
    private String upsModel;
    
    // Automatically set the date before saving
    @PrePersist
    protected void onCreate() {
        allocationDate = LocalDateTime.now();
    }
}
