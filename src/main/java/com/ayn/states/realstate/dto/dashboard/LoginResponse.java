package com.ayn.states.realstate.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

public record LoginResponse (
     String token,
     @JsonProperty("user_name") String username,
     List<String> permissions
){}
