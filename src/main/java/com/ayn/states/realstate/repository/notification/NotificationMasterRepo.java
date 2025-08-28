package com.ayn.states.realstate.repository.notification;


import com.ayn.states.realstate.entity.notification.NotificationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationMasterRepo extends JpaRepository<NotificationMaster,Long> {

@Query("""
 SELECT n, COUNT(d)
         FROM NotificationMaster n
         LEFT JOIN n.notificationDetails d ON(d.seenStatus = true)
         WHERE (n.notificationType = NotificationType.NORMAL_USER OR n.notificationType = NotificationType.ALL)
             AND n.IsActive = true
         GROUP BY n
         ORDER BY n.notificationId DESC
         
""")
List<Object[]> countSeenStatusForNotificationMasters();



    @Query("""
            SELECT nm FROM NotificationMaster nm
            JOIN FETCH nm.notificationDetails nd
            WHERE
            (nd.SendTo = :id OR nd.SendTo = 0) and (nm.notificationType = NotificationType.NORMAL_USER OR nm.notificationType = NotificationType.ALL)
            AND nm.IsActive = true
            ORDER BY nm.CreateAt DESC""")
    List<NotificationMaster> findFilteredNotificationMasters(@Param("id") Integer id);


}
