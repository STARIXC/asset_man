package org.utj.asman.model;

import lombok.Data;
import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "counties")
public class County {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "county_name", nullable = false, unique = true)
    private String countyName;

    @Column(name = "county_code", nullable = false, unique = true)
    private String countyCode;

    @OneToMany(mappedBy = "county", cascade = CascadeType.ALL)
    private List<Facility> facilities;
}
