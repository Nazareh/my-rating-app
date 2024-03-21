package com.turminaz.myratingapp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
@RequiredArgsConstructor
class FirebaseConfig {

    private final FirebaseProperties firebaseProperties;
    @Bean
    FirebaseApp firebaseApp(GoogleCredentials credentials) {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        var app = FirebaseApp.initializeApp(options);

                return app ;
    }

    @Bean
    @DependsOn(value = "firebaseApp")
    FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance() ;
    }


    @Bean
    GoogleCredentials googleCredentials() throws IOException {
        if (firebaseProperties.getServiceAccount() != null) {
            try (InputStream is = firebaseProperties.getServiceAccount().getInputStream()) {
                return GoogleCredentials.fromStream(is);
            }
        }
        else {
            return GoogleCredentials.getApplicationDefault();
        }
    }



}
