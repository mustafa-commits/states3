package com.ayn.states.realstate.repository.propertyRequest;

import com.ayn.states.realstate.entity.propertyRequest.PropertyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRequestRepository extends JpaRepository<PropertyRequest, Long> {


    @Query("SELECT r FROM PropertyRequest r WHERE r.expiryDate >= CURRENT_TIMESTAMP")
    List<PropertyRequest> findActiveRequests();

}
