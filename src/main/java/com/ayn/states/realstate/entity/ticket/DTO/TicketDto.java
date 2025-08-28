package com.ayn.states.realstate.entity.ticket.DTO;

import com.ayn.states.realstate.entity.ticket.Ticket;
import com.ayn.states.realstate.entity.ticket.TicketDetails;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.Date;

public record TicketDto(Ticket ticket, @JsonProperty("ticket_details") TicketDetails ticketDetails,

                        @JsonProperty("not_seen")
                        int NotSeen,
                        int send_or_receive, @JsonProperty("modify_at") Date ModifyAt,  // 0 sender  1 receiver  2 none
                        @JsonProperty("create_user") int createdUser,

                        @JsonProperty("sponsor_name") String Name,
                        @JsonProperty("sponsor_full_name") String FullName,
                        @JsonProperty("create_at") Date createdAt) {

}
