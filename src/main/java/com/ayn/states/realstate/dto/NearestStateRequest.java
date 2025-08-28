package com.ayn.states.realstate.dto;

import jakarta.validation.constraints.NotBlank;

public record NearestStateRequest(
        @NotBlank String latitude,
        @NotBlank String longitude
) {
}
