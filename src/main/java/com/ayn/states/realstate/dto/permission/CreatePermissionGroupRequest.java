package com.ayn.states.realstate.dto.permission;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record CreatePermissionGroupRequest (
     @NotBlank String name,
     @NotNull List<Long> permissionIds
){}
