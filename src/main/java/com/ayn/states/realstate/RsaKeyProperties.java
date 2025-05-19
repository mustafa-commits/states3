package com.ayn.states.realstate;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

//@ConfigurationProperties("rsa")
//public record RsaKeyProperties(RSAPublicKey publickey, RSAPrivateKey privatekey) {
//}

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


//@ConfigurationProperties(prefix = "rsa")
//@Component
//public record RsaKeyProperties(RSAPublicKey publickey, RSAPrivateKey privatekey) {
//}

@Component
@ConfigurationProperties(prefix = "rsa")
@Getter
@Setter
public class RsaKeyProperties {
    private RSAPublicKey publickey;
    private RSAPrivateKey privatekey;



}
