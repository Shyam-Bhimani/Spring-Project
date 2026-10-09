package com.spring.boot.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI loanManagementOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Loan Management API")
                        .description("Rest API for managing Customers and Loans")
                        .version("1.0.0"));
    }
}
