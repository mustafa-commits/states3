package com.ayn.states.realstate.entity.ticket.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ArchiveChatRequest(@JsonProperty("chat_id") int chatId) {
}
