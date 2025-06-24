package com.ayn.states.realstate.repository.user;

import com.ayn.states.realstate.entity.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users,Long> {

    @Query("SELECT u.userId FROM Users u WHERE u.phone LIKE %:phone")
    Optional<Integer> existsByPhoneNumber(@Param("phone") String phone);

    @Query("SELECT u.firstName || ' ' || u.lastName FROM Users u WHERE u.userId = :Id")
    String findNameById(@Param("Id") int id);
}
