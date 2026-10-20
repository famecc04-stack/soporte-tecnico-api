package pe.soporte.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Metadatos de la documentación Swagger (http://localhost:8080/swagger-ui.html). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("API de Solicitudes de Soporte Técnico")
                .version("1.0.0")
                .description("API RESTful para registrar, consultar, actualizar y eliminar solicitudes "
                        + "de soporte técnico. Los datos se almacenan en memoria (sin base de datos).")
                .contact(new Contact().name("Equipo de desarrollo backend")));
    }
}
