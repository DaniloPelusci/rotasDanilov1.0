package com.rotasdanilov.rotas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rotasOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rotas Danilov API")
                        .version("v1")
                        .description("API para cadastro de clientes e otimização de rotas.")
                        .contact(new Contact()
                                .name("Equipe Rotas Danilov")
                                .email("contato@rotasdanilov.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Ambiente local"));
    }
}
