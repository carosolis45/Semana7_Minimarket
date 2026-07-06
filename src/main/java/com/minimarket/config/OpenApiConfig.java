package com.minimarket.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de OpenAPI / Swagger UI. Declara el esquema de seguridad HTTP
 * Basic ("basicAuth") para que, desde Swagger, se pueda autorizar con un usuario
 * y contrasena (admin, cajero, bodeguero o cliente) y probar el control de
 * acceso por rol de los endpoints.
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME = "basicAuth";

    @Bean
    public OpenAPI miniMarketOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MiniMarket Plus API")
                        .version("1.0")
                        .description("API REST con autenticacion por rol (Desarrollo Backend II - Semana 6)"))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME))
                .components(new Components().addSecuritySchemes(SCHEME,
                        new SecurityScheme()
                                .name(SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }
}
