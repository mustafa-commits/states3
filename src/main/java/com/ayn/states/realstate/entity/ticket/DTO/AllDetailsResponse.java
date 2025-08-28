package com.ayn.states.realstate.entity.ticket.DTO;

import com.ayn.states.realstate.entity.ticket.TicketDetails;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AllDetailsResponse(@JsonProperty("ticket_id") Long ticketId, @JsonProperty("ticket_details_list") List<TicketDetails> ticketDetails){}