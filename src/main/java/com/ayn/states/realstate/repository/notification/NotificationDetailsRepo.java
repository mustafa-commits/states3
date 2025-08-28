package com.ayn.states.realstate.repository.notification;


import com.ayn.states.realstate.entity.notification.NotificationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationDetailsRepo extends JpaRepository<NotificationDetails,Long> {
    @Query("SELECT COUNT(nd.Id) FROM NotificationDetails nd where nd.seenStatus=null AND nd.SendTo=:userId")
    Integer findCount(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationDetails nd SET nd.seenStatus = true WHERE nd.SendTo = :updateId AND nd.notificationMaster.notificationId= :notificationId ")
    int SeenNotification(@Param("updateId") Long updateId,@Param("notificationId") Long notificationId);
}
