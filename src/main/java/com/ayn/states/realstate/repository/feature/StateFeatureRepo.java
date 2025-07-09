package com.ayn.states.realstate.repository.feature;


import com.ayn.states.realstate.entity.propertyFeature.PropertyFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateFeatureRepo extends JpaRepository<PropertyFeatures,Long> {
}
