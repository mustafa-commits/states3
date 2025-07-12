package com.ayn.states.realstate.repository.state;

import com.ayn.states.realstate.entity.states.States;
import com.ayn.states.realstate.enums.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface StatesRepo extends JpaRepository<States,Long> {

    @Query(value = "SELECT * FROM States WHERE ST_Distance_Sphere(location, ST_GeomFromText(:long :lat, 4326)) <= :distance", nativeQuery = true)
    List<States> findNearby(@Param("long") String longitude,@Param("lat") String latitude, @Param("distance") double distance);


//    List<States> findByStateTypeAndIsActive(Category category, boolean isActive);




    @Query("""
            SELECT s FROM States s WHERE s.category ='FOR_RENT' AND s.isActive= true""")
    List<States> findByStateTypeAndIsActiveTrue(@Param("type") Category category, Pageable pageable);

    @Query("""
            SELECT s FROM States s
                         LEFT JOIN LookUp l ON (l.typeCode=1 AND l.code=:governate) WHERE s.category =:type AND s.isActive= true""")
    List<States> findByStateTypeAndIsActiveTrueWithGovernate(@Param("type") Category category, @Param("governate") int governate, Pageable pageable);



//    List<States> findByCountryAndStateTypeAndIsActiveTrue(int country, Category category);

    @Query("""
            SELECT s FROM States s WHERE s.isActive= true AND s.stateId=:stateId AND s.publishedAt IS NULL""")
    Optional<States> findByActiveAndPublishedAtIsNull(@Param("stateId") Long stateId);
}
