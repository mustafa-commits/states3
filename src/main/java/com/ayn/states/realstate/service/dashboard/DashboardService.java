package com.ayn.states.realstate.service.dashboard;


import com.ayn.states.realstate.dto.dashboard.CreateUserRequest;
import com.ayn.states.realstate.dto.dashboard.LoginRequest;
import com.ayn.states.realstate.dto.dashboard.LoginResponse;
import com.ayn.states.realstate.entity.dashboard.DashboardUser;
import com.ayn.states.realstate.entity.premission.Permission;
import com.ayn.states.realstate.entity.premission.PermissionGroup;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.ayn.states.realstate.repository.dashboard.DashboardUserRepository;
import com.ayn.states.realstate.repository.dashboard.PermissionGroupRepository;
import com.ayn.states.realstate.service.token.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private DashboardUserRepository userRepo;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PermissionGroupRepository permissionGroupRepository;


    private final BCryptPasswordEncoder encoder;

    public DashboardService() {
        this.encoder = new BCryptPasswordEncoder();
    }


    public LoginResponse login(LoginRequest request) {
        DashboardUser user = userRepo.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!user.isActive() || !encoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        };

        return new LoginResponse(tokenService.generateTokenDashboard(user.getId()),user.getUsername(),
                user.getPermissionGroup()
                        .getPermissions()
                        .stream()
                        .map(Permission::getName)
                        .collect(Collectors.toList()));
    }

    public DashboardUser addUser(String token, CreateUserRequest request) {
        PermissionGroup group = permissionGroupRepository.findByIdAndActiveTrue(request.permissionGroupId())
                .orElseThrow(() -> new UnauthorizedException("Permission group not found"));

        return userRepo.save(DashboardUser.builder()
                .username(request.username())
                .createdBy(Integer.parseInt(tokenService.decodeToken(token.substring(7)).getSubject()))
                .password(encoder.encode(request.password()))
                .email(request.email())
                .permissionGroup(group)
                .active(true)
                .build());

    }

    public List<DashboardUser> listUsers() {
        return userRepo.findAll();
    }
}
