package com.ayn.states.realstate.repository.compound;

import com.ayn.states.realstate.dto.compound.CompoundDTO2;
import com.ayn.states.realstate.dto.compound.CompoundImageDto;
import com.ayn.states.realstate.entity.compound.Compound;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompoundRepository extends JpaRepository<Compound, Long> {


    @Query("""
            SELECT c FROM Compound c WHERE c.isActive= true AND c.id=:CompoundId AND c.approvedAt IS NULL""")
    Optional<Compound> findByActiveAndPublishedAtIsNull(@Param("CompoundId") Long stateId);

    @Query("""
            SELECT c
            FROM Compound c
                WHERE c.approvedAt IS NULL
            """)
    List<Compound> unPublishedCompounds(Pageable pageable);

    @Query("""
            SELECT c
            FROM Compound c
                WHERE c.createUser= :userId
            """)
    List<Compound> findByDashboardUserId(@Param("userId") Long dashboardUserId);

    @Query("""
            SELECT c FROM Compound c WHERE c.isActive= true AND c.id=:compoundId AND c.approvedAt IS NOT NULL""")
    Compound activeCompounds(@Param("compoundId") Long id);

    @Query("""
                SELECT new com.ayn.states.realstate.dto.compound.CompoundDTO2(
                    c,
                    CASE WHEN COUNT(cf) > 0 THEN true ELSE false END
                )
                FROM Compound c
                LEFT JOIN c.followers cf ON cf.user.userId = :userId
                LEFT JOIN FETCH c.features f
                LEFT JOIN FETCH c.posts p
                LEFT JOIN FETCH c.unitMaps um
                WHERE c.isActive = true
                  AND c.id = :compoundId
                  AND c.approvedAt IS NOT NULL
                  
                 ORDER BY p.approvedAt DESC
                """)
    CompoundDTO2 findCompoundWithUserFollow(@Param("compoundId") Long compoundId,
                                            @Param("userId") Long userId);



    @Query("""
            SELECT CASE WHEN COUNT(cf) > 0 THEN true ELSE false END
            FROM CompoundFollower cf
            WHERE cf.compound.id = :compoundId AND cf.user.id = :userId
            """)
    boolean isFollowedByUser(@Param("compoundId") Long compoundId, @Param("userId") Long userId);
}
