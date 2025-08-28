package com.ayn.states.realstate.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
     @NotBlank @JsonProperty("user_name") String username,
     @NotBlank String password
){}


