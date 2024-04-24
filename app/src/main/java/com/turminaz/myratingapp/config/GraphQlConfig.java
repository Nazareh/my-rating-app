package com.turminaz.myratingapp.config;

import com.tailrocks.graphql.datetime.LocalDateTimeScalar;
import graphql.scalars.datetime.DateTimeScalar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
@Configuration
public class GraphQlConfig {
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(DateTimeScalar.INSTANCE);
    }
}
