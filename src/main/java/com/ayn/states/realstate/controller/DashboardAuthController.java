package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.dto.dashboard.CreateUserRequest;
import com.ayn.states.realstate.dto.dashboard.LoginRequest;
import com.ayn.states.realstate.dto.dashboard.LoginResponse;
import com.ayn.states.realstate.entity.dashboard.DashboardUser;
import com.ayn.states.realstate.service.dashboard.DashboardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DashboardAuthController implements SecuredRestController {


    @Autowired
    private DashboardService dashboardService;


    @PostMapping("/V1/api/dashboard/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return dashboardService.login(request);

    }


    @PostMapping("/V1/api/dashboard/AddUser")
    public DashboardUser addUser(@RequestHeader(name = "Authorization") String token, @RequestBody CreateUserRequest request) {
        return dashboardService.addUser(token, request);
    }


    @GetMapping("/V1/api/dashboard/AllUsers")
    public List<DashboardUser> listUsers() {
        return dashboardService.listUsers();

    }


}