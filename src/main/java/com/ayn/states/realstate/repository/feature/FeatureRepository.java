package com.ayn.states.realstate.repository.feature;

import com.ayn.states.realstate.entity.propertyFeature.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRepository extends JpaRepository<Feature,Long> {

    List<Feature> findByIsActiveTrue();

    boolean existsByName(String s);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END FROM Feature f WHERE f.imageUrl = :fileName")
    boolean getFeatureByImageUrl(@Param("fileName") String fileName);
}
