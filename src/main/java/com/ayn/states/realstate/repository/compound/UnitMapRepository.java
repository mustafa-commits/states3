package com.ayn.states.realstate.repository.compound;

import com.ayn.states.realstate.entity.compound.UnitMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitMapRepository extends JpaRepository<UnitMap, Long> {
}
