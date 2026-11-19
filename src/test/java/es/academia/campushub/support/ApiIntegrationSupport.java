// Paquete de utilidades compartidas por los tests de integración.
package es.academia.campushub.support;

// Excepción de entrada/salida que pueden lanzar las llamadas HTTP.
import java.io.IOException;
// Para construir la dirección URL de cada petición.
import java.net.URI;
// Cliente HTTP estándar de Java.
import java.net.http.HttpClient;
// Petición HTTP.
import java.net.http.HttpRequest;
// Respuesta HTTP.
import java.net.http.HttpResponse;
// Expresiones regulares para extraer el id del JSON de respuesta.
import java.util.regex.Pattern;
// Aserciones de JUnit.
import org.junit.jupiter.api.Assertions;
// Arranca la aplicación completa para el test.
import org.springframework.boot.test.context.SpringBootTest;
// Inyecta el puerto aleatorio en el que arranca el servidor.
import org.springframework.boot.test.web.server.LocalServerPort;
// Permite registrar propiedades dinámicas (URLs de los contenedores).
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
// Testcontainers: levanta bases de datos reales en Docker durante los tests.
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Clase base de los tests de integración. Arranca la aplicación en un puerto aleatorio con una
 * base PostgreSQL y otra MongoDB reales (en contenedores Docker) y ofrece métodos para llamar a la
 * API por HTTP.
 */
// Levanta el contexto completo de Spring con servidor web en puerto aleatorio.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Activa la gestión de contenedores Testcontainers.
@Testcontainers
public abstract class ApiIntegrationSupport {
  // Contenedor PostgreSQL 17 compartido por todos los tests.
  @Container
  static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer("postgres:17");

  // Contenedor MongoDB 8 compartido por todos los tests.
  @Container static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:8");

  // Sustituye la configuración de application.yaml por los datos de los contenedores.
  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    // URL, usuario y contraseña de PostgreSQL.
    r.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
    r.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
    r.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
    // URI de conexión de MongoDB.
    r.add("spring.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
  }

  // Puerto aleatorio en el que escucha el servidor durante el test.
  @LocalServerPort int port;
  // Cliente HTTP reutilizado en todas las llamadas.
  private final HttpClient client = HttpClient.newHttpClient();

  // Método genérico: envía una petición HTTP con el método, la ruta y el cuerpo JSON indicados.
  protected HttpResponse<String> call(String method, String endpoint, String json)
      throws IOException, InterruptedException {
    // Prepara la petición apuntando a localhost y marcando el contenido como JSON.
    var b =
        HttpRequest.newBuilder(URI.create("http://localhost:" + port + endpoint))
            .header("Content-Type", "application/json");
    // Si no hay cuerpo se envía vacío; si lo hay, se envía el JSON como texto.
    var req =
        b.method(
                method,
                json == null
                    ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(json))
            .build();
    // Envía la petición y devuelve la respuesta como texto.
    return client.send(req, HttpResponse.BodyHandlers.ofString());
  }

  // Atajo para peticiones POST.
  protected HttpResponse<String> post(String path, String json) throws Exception {
    return call("POST", path, json);
  }

  // Atajo para peticiones GET (sin cuerpo).
  protected HttpResponse<String> get(String path) throws Exception {
    return call("GET", path, null);
  }

  // Atajo para peticiones PUT.
  protected HttpResponse<String> put(String path, String json) throws Exception {
    return call("PUT", path, json);
  }

  // Comprueba que el código de estado de la respuesta es el esperado (muestra el cuerpo si falla).
  protected void status(int expected, HttpResponse<String> actual) {
    Assertions.assertEquals(expected, actual.statusCode(), actual.body());
  }

  // Extrae el primer campo id numérico del JSON de respuesta.
  protected long id(HttpResponse<String> r) {
    // Busca el patrón "id": número.
    var m = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(r.body());
    // Falla el test si la respuesta no contiene id.
    Assertions.assertTrue(m.find(), "Falta id en respuesta: " + r.body());
    // Devuelve el número encontrado.
    return Long.parseLong(m.group(1));
  }
}
