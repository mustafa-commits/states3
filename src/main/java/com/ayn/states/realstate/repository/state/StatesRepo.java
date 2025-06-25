package com.ayn.states.realstate.repository.state;

import com.ayn.states.realstate.entity.states.States;
import com.ayn.states.realstate.enums.StateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatesRepo extends JpaRepository<States,Long> {

    @Query(value = "SELECT * FROM States WHERE ST_Distance_Sphere(location, ST_GeomFromText(:long :lat, 4326)) <= :distance", nativeQuery = true)
    List<States> findNearby(@Param("long") String longitude,@Param("lat") String latitude, @Param("distance") double distance);


    /**
     * Find properties by state type and active status
     * 
     * @param stateType the type of property (FOR_SALE, FOR_RENT, etc)
     * @param isActive whether the property is active
     * @return list of matching properties
     */
    List<States> findByStateTypeAndIsActive(StateType stateType, boolean isActive);




    @Query("""
            SELECT s FROM States s WHERE s.stateType ='FOR_RENT' AND s.isActive= true""")
    List<States> findByStateTypeAndIsActiveTrue(@Param("type") StateType stateType, Pageable pageable);

    @Query("""
            SELECT s FROM States s
                         LEFT JOIN LookUp l ON (l.typeCode=1 AND l.code=:governate) WHERE s.stateType =:type AND s.isActive= true""")
    List<States> findByStateTypeAndIsActiveTrueWithGovernate(@Param("type") StateType stateType,@Param("governate") int governate, Pageable pageable);



    List<States> findByCountryAndStateTypeAndIsActiveTrue(int country, StateType stateType);
}
