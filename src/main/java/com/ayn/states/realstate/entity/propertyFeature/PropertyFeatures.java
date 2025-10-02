package com.ayn.states.realstate.entity.propertyFeature;


import com.ayn.states.realstate.entity.states.States;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PROPERTY_FEATURES")
public class PropertyFeatures {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long featureId;

    private String featureName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id") // Foreign key column
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private States featuredState;


    public PropertyFeatures(String featureName, States featuredState) {
        this.featureName = featureName;
        this.featuredState = featuredState;
    }

    }
