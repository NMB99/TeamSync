package com.teamsync.teamsync.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI teamSyncOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TeamSync API")
                        .description("""
                                Daily standup automation backend. Built with Spring Boot, JWT auth, and role-based access control.
                                
                                **Visitor credentials (TEAM_LEAD role):**
                                - Email: `visitor@teamsync.com`
                                - Password: `Visitor@1234`
                                
                                Use `POST /api/auth/login` to get a JWT token, then click **Authorize** and paste it.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nilay Bhaisare")
                                .url("https://github.com/NMB99/TeamSync")))
                .servers(List.of(
                        new Server().url("https://teamsync-api.up.railway.app").description("Deployed (Railway)"),
                        new Server().url("http://localhost:8080").description("Local development")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
