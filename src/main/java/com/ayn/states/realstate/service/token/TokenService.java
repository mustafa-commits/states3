package com.ayn.states.realstate.service.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;


    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;

    }

    public String generateToken(Authentication authentication, int id) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofDays(365L * 5));

        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                        .issuer("AL-AYN.org")
                        .audience(List.of("development_center"))
                        .issuedAt(now)
                        .expiresAt(expiry)
                        .subject(Integer.toString(id))
                        .claim("scope", "zoneUser")
                        .build()
        )).getTokenValue();
    }


    public Jwt decodeToken(String token) {
        return jwtDecoder.decode(token);


    }


}
