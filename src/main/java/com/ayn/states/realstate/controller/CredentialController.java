package com.ayn.states.realstate.controller;

import com.ayn.states.realstate.SecuredRestController;
import com.ayn.states.realstate.entity.verification.DTO.VerificationRequest;
import com.ayn.states.realstate.entity.verification.DTO.VerificationResponse;
import com.ayn.states.realstate.enums.SignUpStatus;
import com.ayn.states.realstate.exception.UnauthorizedException;
import com.ayn.states.realstate.models.user.UserCheckNumber;
import com.ayn.states.realstate.models.user.UserNumberCheckRequest;
import com.ayn.states.realstate.service.token.TokenService;
import com.ayn.states.realstate.service.users.UserService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CredentialController implements SecuredRestController {


    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

    @GetMapping("/V1/api/getToken")
    public String getToken(Authentication authentication) {
        return tokenService.generateToken(authentication, 0);
    }


    @PostMapping("/V1/api/app/login")
    public ResponseEntity<UserCheckNumber> login(@RequestBody @Validated UserNumberCheckRequest userNumbercheckRequest) {

        return ResponseEntity.ok(userService.login(userNumbercheckRequest.number(), userNumbercheckRequest.countryCode(), userNumbercheckRequest.codeSend()));

    }


    @PostMapping("/V1/api/verification")
    public VerificationResponse verification(@RequestBody VerificationRequest verificationRequest) {
        return userService.verification(verificationRequest);
    }


    @PostMapping("/V1/api/user/signUp")
    public SignUpStatus signUp(@RequestBody SignUpRequest signUpRequest){
        return userService.signUp(signUpRequest);
    }

    public record SignUpRequest(
            @JsonProperty("first_name") String firstName,

            @JsonProperty("last_name") String lastName,

            @JsonProperty("phone_number") String phoneNumber,

            int country,

            int governorate
    ){}

    //todo add list lookup of all possible countries and gov




}
