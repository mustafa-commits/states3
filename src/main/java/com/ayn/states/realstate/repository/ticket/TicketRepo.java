package com.ayn.states.realstate.repository.ticket;

import com.ayn.states.realstate.entity.ticket.Ticket;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepo extends JpaRepository<Ticket,Integer> {
    @Query("select t from Ticket t where t.userId= ?1 and t.IsActive =true order by t.updateAt DESC")
    List<Ticket> findAllByuserIId(int userId);

//    @Query("""
//            SELECT t, td, d.delegateName FROM Ticket t
//            LEFT JOIN TicketDetails td ON t.Id = td.ticketId
//            LEFT JOIN TmEcDelegate d ON t.CreatedUser = d.delegateId
//            WHERE t.userId = :userId  and td.createAt = (SELECT MAX(td2.createAt) FROM TicketDetails td2 WHERE td2.ticketId = t.Id) AND t.IsActive=true
//            GROUP BY t, td, d.delegateName ,d.delegateId
//            ORDER BY td.createAt DESC""")
//    List<Object[]> findAllByuserIdqueryold(@Param("userId") Long userId);

    @Query("""
            SELECT t, td FROM Ticket t
            LEFT JOIN TicketDetails td ON t.Id = td.ticketId
            WHERE t.userId = :userId  and td.createAt = (SELECT MAX(td2.createAt) FROM TicketDetails td2 WHERE td2.ticketId = t.Id) AND t.IsActive=true
            GROUP BY t, td
            ORDER BY td.createAt DESC""")
    List<Object[]> findAllByuserIdquery(@Param("userId") int userId);

    @Query("select t from Ticket t where t.userId= ?1 and t.IsActive = false order by t.updateAt DESC")
    List<Ticket> findAllByuserIIdClosed(int ticketId);

    @Query("select t from Ticket t where t.Id= ?1 and t.IsActive = true order by t.updateAt DESC")
    Ticket findById2(@NotNull int id);

    @Query("SELECT t, td, u.firstName || ' ' || u.lastName, t.ticketType " +
           "FROM Ticket t " +
           "LEFT JOIN TicketDetails td ON t.Id = td.ticketId " +
           "LEFT JOIN Users u ON u.userId=t.userId " +
           "WHERE td.createAt = (SELECT MAX(td2.createAt) FROM TicketDetails td2 WHERE td2.ticketId = t.Id) AND t.IsActive = true " +
           "GROUP BY t, td, u.firstName || ' ' || u.lastName, t.ticketType " +
           "ORDER BY td.createAt DESC")
    List<Object[]> findAllTicketsWithLastDetails();

//    @Query("SELECT t, td, d.delegateName FROM Ticket t " +
//           "LEFT JOIN TicketDetails td ON t.Id = td.ticketId " +
//           "LEFT JOIN TmEcDelegate d ON t.CreatedUser = d.delegateId " +
//           "WHERE td.createAt = (SELECT MAX(td2.createAt)  FROM TicketDetails td2 WHERE td2.ticketId = t.Id) AND t.IsActive=false " +
//           "GROUP BY t, td, d.delegateName " +
//           "ORDER BY td.createAt DESC")
//    List<Object[]> findAllTicketsWithLastDetailsColsed(); //AND t.IsActive=0

    @Query("SELECT t.Id FROM Ticket t WHERE t.userId = :userId")
    Long existsByUserId(int userId);
}
