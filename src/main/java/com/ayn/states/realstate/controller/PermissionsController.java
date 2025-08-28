package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.dto.permission.CreatePermissionGroupRequest;
import com.ayn.states.realstate.dto.permission.CreatePermissionRequest;
import com.ayn.states.realstate.entity.premission.Permission;
import com.ayn.states.realstate.entity.premission.PermissionGroup;
import com.ayn.states.realstate.service.permissions.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class PermissionsController implements SecuredRestController {


    @Autowired
    private PermissionService permissionService;


    @GetMapping("/V1/api/AllPermissions")
    public List<Permission> listPermissions() {
        return permissionService.listPermissions();

    }


    @GetMapping("/V1/api/AllPermissionGroups")
    public List<PermissionGroup> listGroups() {
        return permissionService.listGroups();
    }


    @PostMapping("/V1/api/AddPermission")
    public Permission addPermission(@RequestHeader(name = "Authorization") String token,@RequestBody @Valid CreatePermissionRequest request) {
        return permissionService.addPermission(request,token);

    }


    @PostMapping("/V1/api/AddPermissionGroups")
    public PermissionGroup addPermissionGroup(@RequestHeader(name = "Authorization") String token ,@RequestBody @Valid CreatePermissionGroupRequest request) {
        return permissionService.addPermissionGroup(request,token);

    }

    @PutMapping("/V1/api/PermissionGroup/{id}")
    public PermissionGroup updatePermissionGroup(
            @PathVariable Long id,
            @RequestBody @Valid CreatePermissionGroupRequest request, @RequestHeader(name = "Authorization") String token) {
        return permissionService.updatePermissionGroup(id, request,token);
    }

}
