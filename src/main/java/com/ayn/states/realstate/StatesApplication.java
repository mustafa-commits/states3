package com.ayn.states.realstate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class StatesApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(StatesApplication.class, args);
	}


}


//@ConfigurationProperties(prefix = "rsa")
//record RsaKeyProperties(RSAPublicKey publickey, RSAPrivateKey privatekey) {
//}