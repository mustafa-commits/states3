package com.ayn.states.realstate.repository.compound;

import com.ayn.states.realstate.entity.compound.CompoundFollower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompoundFollowerRepository extends JpaRepository<CompoundFollower, Long> {

    @Query("SELECT CASE WHEN COUNT(cf) > 0 THEN true ELSE false END " +
            "FROM CompoundFollower cf " +
            "WHERE cf.compound.id = :compoundId AND cf.user.userId = :userId")
    boolean existsByCompoundAndUser(@Param("compoundId") Long compoundId,
                                    @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM CompoundFollower cf " +
            "WHERE cf.compound.id = :compoundId AND cf.user.userId = :userId")
    int deleteByCompoundAndUser(@Param("compoundId") Long compoundId,
                                @Param("userId") Long userId);
}

