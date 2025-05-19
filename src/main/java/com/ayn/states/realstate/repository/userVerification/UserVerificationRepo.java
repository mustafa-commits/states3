package com.ayn.states.realstate.repository.userVerification;

import com.ayn.states.realstate.entity.verification.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVerificationRepo extends JpaRepository<UserVerification,Integer> {


    @Query("""
            SELECT uv FROM UserVerification uv
             WHERE uv.userId = :userid
             order by createDate desc limit 1""")
    UserVerification findFirstByUserid(@Param("userid") long userid);



}
