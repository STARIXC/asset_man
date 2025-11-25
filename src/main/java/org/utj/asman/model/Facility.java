package org.utj.asman.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data // Generates Getters, Setters, ToString, etc. automatically
@Table(name = "facilities")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "facility_name", nullable = false)
    private String facilityName;

    // MFL Code must be unique so we don't have duplicate hospitals
    @Column(name = "mfl_code", nullable = false, unique = true)
    private String mflCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "county_id")
    private County county;
}
