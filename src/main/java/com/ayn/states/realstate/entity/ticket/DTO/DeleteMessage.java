package com.ayn.states.realstate.entity.ticket.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteMessage(@JsonProperty("message_id") int messageId) {
}
