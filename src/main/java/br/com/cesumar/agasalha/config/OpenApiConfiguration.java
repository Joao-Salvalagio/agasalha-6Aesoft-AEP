package br.com.cesumar.agasalha.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI agasalhaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Agasalha API")
                .description("Mural de doacao e matching de agasalhos")
                .version("v1"));
    }
}
