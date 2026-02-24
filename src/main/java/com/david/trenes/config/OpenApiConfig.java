package com.david.trenes.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI trenesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Trenes")
                        .version("1.0.0")
                        .description("Documentación OpenAPI/Swagger de la aplicación Trenes"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                        .addSchemas("LocalDateTime", new StringSchema()
                                .format("date")
                                .pattern("dd/MM/yyyy HH:mm:ss")
                                .example("27/02/2026 14:30:00")
                                .description("Fecha y hora en formato español (dd/MM/yyyy HH:mm:ss). También acepta formato solo fecha (dd/MM/yyyy) y formato ISO (yyyy-MM-ddTHH:mm:ss)"))
                );
    }
}
