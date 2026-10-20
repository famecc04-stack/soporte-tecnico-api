package pe.soporte.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API de Solicitudes de Soporte Técnico.
 * Los datos se mantienen en memoria (Map) mientras la aplicación está en ejecución.
 */
@SpringBootApplication
public class SoporteTecnicoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoporteTecnicoApiApplication.class, args);
    }
}
