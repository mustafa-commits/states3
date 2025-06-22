package com.ayn.states.realstate.repository.state;

import com.ayn.states.realstate.entity.states.States;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatesRepo extends JpaRepository<States,Long> {

    @Query(value = "SELECT * FROM States WHERE ST_Distance_Sphere(location, ST_GeomFromText(:long :lat, 4326)) <= :distance", nativeQuery = true)
    List<States> findNearby(@Param("long") String longitude,@Param("lat") String latitude, @Param("distance") double distance);

}
