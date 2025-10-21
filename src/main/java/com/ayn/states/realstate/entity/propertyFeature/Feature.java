package com.ayn.states.realstate.entity.propertyFeature;

import com.ayn.states.realstate.entity.states.States;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "FEATURES")
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long featureId;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    private FeatureType featureType;

    private String imageUrl;

    @Column(columnDefinition = "TINYINT default 1", nullable = false)
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private long createdBy;

    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    private Long modifiedBy;

//    @ManyToMany(mappedBy = "features")
//    @JsonIgnore
//    private Set<States> states = new HashSet<>();

    public Feature(String name, FeatureType featureType,long createdBy,String imageUrl) {
        this.name = name;
        this.featureType = featureType;
        this.createdBy=createdBy;
        this.imageUrl=imageUrl;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

