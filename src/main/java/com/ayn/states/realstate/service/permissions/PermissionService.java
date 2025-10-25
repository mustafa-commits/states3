package com.ayn.states.realstate.service.permissions;


import com.ayn.states.realstate.dto.permission.CreatePermissionGroupRequest;
import com.ayn.states.realstate.dto.permission.CreatePermissionRequest;
import com.ayn.states.realstate.entity.premission.Permission;
import com.ayn.states.realstate.entity.premission.PermissionGroup;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.ayn.states.realstate.repository.dashboard.PermissionGroupRepository;
import com.ayn.states.realstate.repository.dashboard.PermissionRepository;
import com.ayn.states.realstate.service.token.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PermissionService {


    @Autowired
    private PermissionGroupRepository permissionGroupRepository;

    @Autowired
    private PermissionRepository permissionRepo;

    @Autowired
    private TokenService tokenService;


    @Transactional
    public PermissionGroup addPermissionGroup(CreatePermissionGroupRequest request, String token) {
        return permissionGroupRepository.save(PermissionGroup.builder()
                .name(request.name())
                .permissions(new HashSet<>(permissionRepo.findAllById(request.permissionIds())))
                .createdBy(Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()))
                .build());

    }

    public Permission addPermission(CreatePermissionRequest request, String token) {
        return permissionRepo.save(Permission.builder()
                .name(request.name())
                .description(request.description())
                .createdBy(Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()))
                .build());
    }

    @Transactional
    public PermissionGroup updatePermissionGroup(Long id, CreatePermissionGroupRequest request, String token) {
        PermissionGroup existingGroup = permissionGroupRepository.findById(id)
                .orElseThrow(() -> new UnauthorizedException("Permission group not found: " + id));

        if (!existingGroup.getName().equals(request.name())) {
            existingGroup.setName(request.name());
        }

        Set<Permission> newPermissions = new HashSet<>(permissionRepo.findAllById(request.permissionIds()));

        if (!existingGroup.getPermissions().equals(newPermissions)) {
            existingGroup.setPermissions(newPermissions);
            existingGroup.setUpdatedAt(LocalDateTime.now());
            existingGroup.setUpdatedBy(Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()));
        }

        return existingGroup;
    }


    public List<PermissionGroup> listGroups() {
//        return permissionGroupRepository.findAllByActive(true);
        return permissionGroupRepository.findAll();
    }

    public List<Permission> listPermissions() {
        return permissionRepo.findAllByActive(true);
    }
}
