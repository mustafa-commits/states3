package com.ayn.states.realstate.repository.compound;

import com.ayn.states.realstate.entity.compound.CompoundPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CompoundPostRepo extends JpaRepository<CompoundPost,Long> {

    @Modifying
    @Query("UPDATE CompoundPost c SET c.approvedAt = :approvedAt, c.approvedUser = :approvedUser WHERE c.postId = :postId")
    void approvePost(@Param("postId") Long postId, @Param("approvedAt") LocalDateTime approvedAt, @Param("approvedUser") Integer approvedUser);

}
