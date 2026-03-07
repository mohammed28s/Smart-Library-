package com.smartlibrary.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartLibraryOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartLibrary API")
                        .description("REST API documentation for SmartLibrary backend")
                        .version("v1")
                        .license(new License().name("MIT")));
    }
}
