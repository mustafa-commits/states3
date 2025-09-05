package com.ayn.states.realstate.repository.ticket;

import com.ayn.states.realstate.entity.ticket.TicketDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketDetailsRepo extends JpaRepository<TicketDetails, Integer> {

    @Query("""
           select new com.ayn.states.realstate.entity.ticket.TicketDetails(td.Id,td.Content ,td.ticketId,td.messageType, td.Sender, td.Receiver, td.createAt,td.ModifiedAt, td.createdUser,td.ModifiedUser, td.IsActive, td.seenDate,td.replayId,
           CASE WHEN td.messageType = MessageType.TEXT THEN tr.Content
           WHEN td.messageType = MessageType.M4A THEN 'http://72.60.81.126:8080/V1/api/stream-audio/' || tr.Content
           ELSE 'http://72.60.81.126:8080/V1/api/downloadImg/' || tr.Content END ) from TicketDetails td
           LEFT JOIN TicketDetails tr on (tr.Id=td.replayId)
           where td.ticketId = :ticketId order by td.createAt DESC""")
    List<TicketDetails> findAllByTicketIdOrderByIdDesc(@Param("ticketId")int ticketId, Pageable paging);


    @Query("select t from TicketDetails t where t.ticketId = ?1 order by t.ModifiedAt DESC limit 1")
    Optional<TicketDetails> findFirstByTicketIdOrderByModifiedAtDesc(int ticketId);


    @Query("SELECT COUNT(td) FROM TicketDetails td WHERE td.seenDate IS NULL AND td.Receiver = 0 GROUP BY td.ticketId")
    List<Integer> findAllNotSeen();

    @Query("SELECT COUNT(td) FROM TicketDetails td LEFT JOIN Ticket t on (t.Id = td.ticketId) WHERE td.seenDate IS NULL AND td.Receiver != 0 AND td.Receiver = :userId AND t.IsActive=true")
    Integer findAllNotSeenUser(@Param("userId") int userId);

    @Query("SELECT COUNT(td) FROM TicketDetails td WHERE td.seenDate IS NULL AND td.Receiver = 0 AND td.ticketId= :ticketId")
    Integer findAllNotSeenWithTicket(@Param("ticketId") int ticketId);


    @Modifying
    @Transactional
    @Query("UPDATE TicketDetails td SET td.seenDate = CURRENT_TIMESTAMP WHERE td.seenDate IS NULL AND td.ticketId= :ticketId AND td.Receiver= :value")
    void setTicketAsSeen(@Param("ticketId") int ticketId,@Param("value") int value);

    @Modifying
    @Transactional
    @Query("UPDATE TicketDetails td SET td.seenDate = CURRENT_TIMESTAMP WHERE td.seenDate IS NULL AND td.ticketId= ?1 AND td.Receiver != 0")
    void setTicketAsSeenMobile(int ticketId);
}
