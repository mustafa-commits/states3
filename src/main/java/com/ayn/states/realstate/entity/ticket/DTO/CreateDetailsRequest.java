package com.ayn.states.realstate.entity.ticket.DTO;

import jakarta.validation.constraints.NotBlank;

public record CreateDetailsRequest(@NotBlank String content,
                                   int receiver, int ticket_id) {
}
