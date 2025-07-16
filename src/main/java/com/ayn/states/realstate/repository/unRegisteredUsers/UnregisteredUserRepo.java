package com.ayn.states.realstate.repository.unRegisteredUsers;

import com.ayn.states.realstate.entity.unregisterUsers.UnregisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UnregisteredUserRepo extends JpaRepository<UnregisteredUser,Long> {
}
