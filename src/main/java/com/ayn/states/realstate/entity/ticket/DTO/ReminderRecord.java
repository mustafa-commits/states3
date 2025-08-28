package com.ayn.states.realstate.entity.ticket.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReminderRecord(@JsonProperty("chat_id") int chatId) {
}
