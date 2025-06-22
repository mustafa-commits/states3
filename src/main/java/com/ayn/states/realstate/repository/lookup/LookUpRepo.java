package com.ayn.states.realstate.repository.lookup;

import com.ayn.states.realstate.entity.lookup.LookUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LookUpRepo extends JpaRepository<LookUp,Long> {
}
