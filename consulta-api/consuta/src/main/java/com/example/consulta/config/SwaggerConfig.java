package com.example.consulta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API do Sistema de Consultas Médicas")
                .version("v1.0")
                .description("Documentação da API REST para gerenciamento de consultas médica.")
                .contact(new Contact()
                    .name("Trabalho de WEB II")
                    .url("https://github.com/Rafadiassss/Consulta"))
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

}
