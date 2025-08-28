package com.ayn.states.realstate;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@SpringBootApplication
@EnableScheduling
//@EnableCaching
//@EnableAsync
public class StatesApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(StatesApplication.class, args);


    }
    //todo: link un registered to their real account
    //todo: refresh token


    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {


        GoogleCredentials googleCredentials=GoogleCredentials.fromStream(
                new ClassPathResource("firebase-service-account.json").getInputStream()
        );
        FirebaseOptions firebaseOptions= FirebaseOptions.builder()
                .setCredentials(googleCredentials).build();
        FirebaseApp app=FirebaseApp.initializeApp(firebaseOptions,"sponsor");
        return  FirebaseMessaging.getInstance(app);
    }


}


//@ConfigurationProperties(prefix = "rsa")
//record RsaKeyProperties(RSAPublicKey publickey, RSAPrivateKey privatekey) {
//}