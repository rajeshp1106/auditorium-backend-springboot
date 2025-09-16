package com.game.project.auth.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LevelUp Backend API")
                        .version("1.0.0")
                        .description("Guild-based Coding Platform with Auth, OTP, JWT")
                        .contact(new Contact().name("Rajesh P").email("levelupcodeproject@gmail.com")));
    }
}
