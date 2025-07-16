package com.ayn.states.realstate.repository.lookup;

import com.ayn.states.realstate.entity.lookup.LookUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LookUpRepo extends JpaRepository<LookUp,Long> {


    @Query("""
            SELECT l FROM LookUp l WHERE l.typeCode=1 AND l.parent IS NULL AND l.isActive= true""")
    List<LookUp> findByTypeCodeCustom();

}
