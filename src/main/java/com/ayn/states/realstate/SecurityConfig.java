package com.ayn.states.realstate;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    public final RsaKeyProperties rsaKeys;


    public SecurityConfig(RsaKeyProperties rsaKeys) {
        this.rsaKeys = rsaKeys;
    }


    @Bean
    public InMemoryUserDetailsManager user() {
        return new InMemoryUserDetailsManager(
                User.withUsername("mustafa")
                        .password("{noop}password")
                        .authorities("read")
                        .build()
        );
    }


@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/login").hasAuthority("read")
                    .requestMatchers("/V1/api/dashboard/auth/login/**","/V1/api/getToken/**").permitAll()
                    .requestMatchers("/v3/api-docs/**","/swagger-ui","/swagger-ui/**").permitAll()
                    .requestMatchers("/V1/api/getToken","/V1/api/resetCache/**").permitAll()
                    .requestMatchers("/V1/api/settings/GetSupportContact/**").permitAll()
                    .requestMatchers("/V1/api/stream-audio/**","/V1/api/stateAttachment/**").permitAll()
                    .anyRequest().authenticated()
            ).cors(withDefaults())
            .oauth2ResourceServer((oauth2) -> oauth2
                    .jwt(Customizer.withDefaults())
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(withDefaults())
            .build();
}

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.getPublickey()).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.getPublickey()).privateKey(rsaKeys.getPrivatekey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public static String refreshJwt(String expiredToken, String refreshToken) throws ParseException, JOSEException {

        // Validate refresh token (e.g., check against a secure storage)

        // Parse the expired JWT token
        SignedJWT signedJWT = SignedJWT.parse(expiredToken);

        // Validate the JWT signature
        JWSVerifier verifier = new MACVerifier("your_jwt_secret");

        if (!signedJWT.verify(verifier)) {
            throw new JOSEException("Invalid JWT signature");
        }

        // Get the JWT claims
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        // Check the expiration time and audience (optional)
        if (claimsSet.getExpirationTime().before(new Date())) {
            throw new JOSEException("JWT token has already expired");
        }

        // Create a new JWT token with updated claims
        JWTClaimsSet newClaimsSet = new JWTClaimsSet.Builder()
                .subject(claimsSet.getSubject())
                .issuer(claimsSet.getIssuer())
                .expirationTime(new Date(System.currentTimeMillis() + 900000)) // Set new expiration time (e.g., 15 minutes from now)
                .claim("customKey", claimsSet.getClaim("customKey")) // Copy any custom claims
                .build();

        signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), newClaimsSet);

        // Sign the JWT with your secret key
        signedJWT.sign(new MACSigner("your_jwt_secret"));

        // Serialize the new JWT token
        return signedJWT.serialize();
    }


}