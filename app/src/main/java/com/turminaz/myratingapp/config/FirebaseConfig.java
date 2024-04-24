package com.turminaz.myratingapp.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
class FirebaseConfig {

    @Value("${spring.cloud.gcp.project-id:#{null}}")
    private String projectId;

    @Bean
    FirebaseApp firebaseApp() throws IOException {
        FirebaseOptions options;
            options = FirebaseOptions.builder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build();
            return FirebaseApp.initializeApp(options);
    }
    @Bean
    @ConditionalOnProperty(
            value = "spring.cloud.gcp.firestore.emulator.enabled",
            havingValue = "true")
    public CredentialsProvider googleCredentials() {
        return NoCredentialsProvider.create();
    }

    @Bean
    @DependsOn(value = "firebaseApp")
    FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance() ;
    }

    @Bean
    @DependsOn(value = "firebaseApp")
    Firestore firestore() {
        return FirestoreClient.getFirestore() ;
    }
}
