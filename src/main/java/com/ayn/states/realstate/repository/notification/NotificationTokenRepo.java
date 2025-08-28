package com.ayn.states.realstate.repository.notification;


import com.ayn.states.realstate.entity.notification.NotificationToken;
import com.ayn.states.realstate.entity.notification.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTokenRepo extends JpaRepository<NotificationToken,Integer> {



    @Query("select n.Token from NotificationToken n where n.userId = ?1 AND n.tokenType = TokenType.MOBILE")
    Optional<String> findTokenByID(Long BeneficentNo);

    @Query("select n from NotificationToken n where n.tokenType =:tokenType ")
    List<NotificationToken> findAllByTokenType(@Param("tokenType")TokenType attr0);
}
