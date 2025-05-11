package tech.rket.shared.infrastructure.oas;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SwaggerConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public OpenAPI currentOpenAPI() {
        OpenAPI openAPI = new OpenAPI();

        String serverUrl = env.getProperty("springdoc.swagger-ui.server-url");
        if (serverUrl != null && !serverUrl.isEmpty()) {
            openAPI.addServersItem(new Server().url(serverUrl));
        }

        openAPI.components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));

        return openAPI;
    }
}
