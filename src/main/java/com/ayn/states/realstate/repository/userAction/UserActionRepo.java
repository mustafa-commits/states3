package com.ayn.states.realstate.repository.userAction;

import com.ayn.states.realstate.entity.fav.UserActions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepo extends JpaRepository<UserActions, Long> {
}
