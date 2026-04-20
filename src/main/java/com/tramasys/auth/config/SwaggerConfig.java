package com.tramasys.auth.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
    name = "bearerAuth", 
    type = SecuritySchemeType.HTTP, 
    bearerFormat = "JWT", 
    scheme = "bearer"
)
public class SwaggerConfig {

    // On injecte la valeur définie dans application.properties
    @Value("${app.swagger.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TraMaSys Auth API")
                        .version("v1")
                        .description("Service d'authentification centralisé"))
                .servers(List.of(
                        new Server().url(serverUrl).description("Serveur défini par environnement"),
                        new Server().url("/").description("Serveur Local (Relatif)")
                ));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("toutes-les-routes")
                .pathsToMatch("/**")
                .packagesToScan("com.tramasys.auth.adapters.in.web")
                .build();
    }
}