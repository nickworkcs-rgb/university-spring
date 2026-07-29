package ru.project.university.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI universityOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("University API")
                        .description("REST API for managing faculties, students and books")
                        .version("v0.0.1"));
    }
}