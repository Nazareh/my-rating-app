package com.turminaz.myratingapp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
class FirebaseConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.credentials.location}")
    private Resource credentials;

    @Bean
    FirebaseApp firebaseApp() throws IOException {
        FirebaseOptions options;
        if (credentials == null) {
            options = FirebaseOptions.builder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build();
            return FirebaseApp.initializeApp(options);
        }

        try (InputStream is = credentials.getInputStream() ) {
            var credentials = GoogleCredentials.fromStream(is);

            options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build();
        }
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    @DependsOn(value = "firebaseApp")
    FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance() ;
    }
}
