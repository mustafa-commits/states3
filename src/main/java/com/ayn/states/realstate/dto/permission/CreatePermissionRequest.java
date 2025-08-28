package com.ayn.states.realstate.dto.permission;


import jakarta.validation.constraints.NotBlank;

public record CreatePermissionRequest (
     @NotBlank String name,
     @NotBlank String description
){}
