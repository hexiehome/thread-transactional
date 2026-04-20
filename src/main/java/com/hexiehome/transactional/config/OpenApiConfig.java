package com.hexiehome.transactional.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI transactionDemoApi() {
        return new OpenAPI().info(new Info()
                .title("Thread Transactional Teaching Demo")
                .description("Scenario-driven demo for coordinated multi-thread transaction decisions")
                .version("2.0.0"));
    }
}
