package com.eduardo.escuela.configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
@Configuration 
public class OpenApiConfig {
    @Bean 
    public OpenAPI openApi(){
        return new OpenAPI().info(new Info()
            .title("API de escuela")
            .version("1.0.0")
            .description("Api para la gestion el registro de la escuela"));
    } 
}
