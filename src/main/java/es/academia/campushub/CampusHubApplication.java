// Paquete raíz de la aplicación CampusHub; Spring escanea este paquete y sus subpaquetes.
package es.academia.campushub;

// Clase de Spring Boot que permite arrancar la aplicación desde el método main.
import org.springframework.boot.SpringApplication;
// Anotación que activa la autoconfiguración, el escaneo de componentes y la configuración de Spring.
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de CampusHub.
 * <p>
 * Es el punto de entrada de la aplicación: arranca el servidor web embebido, conecta con
 * PostgreSQL, MongoDB y Redis (según application.yaml) y registra controladores, servicios
 * y repositorios.
 */
// Marca la clase como aplicación Spring Boot (@Configuration + @EnableAutoConfiguration + @ComponentScan).
@SpringBootApplication
public class CampusHubApplication {
  // Método main: es lo primero que ejecuta la JVM al lanzar el programa.
  public static void main(String[] args) {
    // Crea el contexto de Spring, levanta el servidor (puerto 8080 por defecto) y deja la API escuchando.
    SpringApplication.run(CampusHubApplication.class, args);
  }
}
