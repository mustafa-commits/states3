package com.ayn.states.realstate.dto.dashboard;


import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateUserRequest (
     @JsonProperty("user_name") String username,
     String password,
     String email,
     @JsonProperty("permission_group_id") Long permissionGroupId
){}
