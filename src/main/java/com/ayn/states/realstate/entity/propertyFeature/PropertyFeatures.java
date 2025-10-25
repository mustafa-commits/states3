package com.ayn.states.realstate.entity.propertyFeature;


import com.ayn.states.realstate.entity.compound.Compound;
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
    private Long Id;

    private Long featureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id") // Foreign key column
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private States featuredState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id") // Foreign key column
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private Compound featuredCompound;


    public PropertyFeatures(Long featureId, States featuredState) {
        this.featureId = featureId;
        this.featuredState = featuredState;
    }

    public PropertyFeatures(Long featureId, Compound featuredCompound) {
        this.featureId = featureId;
        this.featuredCompound = featuredCompound;
    }

    }
