package org.utj.asman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import javax.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

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
    @ToString.Exclude
    @JsonIgnore
    private County county;

    @CreationTimestamp
    @Column(name = "created_at", nullable = true, updatable = false)
    private LocalDateTime createdAt;
}
