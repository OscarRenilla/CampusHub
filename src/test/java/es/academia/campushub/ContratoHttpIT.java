// Paquete raíz de los tests de la aplicación.
package es.academia.campushub;

// Clase base con la API de pruebas HTTP y los contenedores de base de datos.
import es.academia.campushub.support.ApiIntegrationSupport;
// Anotación que marca un método como test de JUnit.
import org.junit.jupiter.api.Test;

/** Tests del contrato HTTP general: tratamiento de errores comunes por la API. */
class ContratoHttpIT extends ApiIntegrationSupport {
  // Un JSON mal formado debe devolver 400 (Bad Request).
  @Test
  void jsonMalformadoEs400() throws Exception {
    // El cuerpo { está incompleto y no es JSON válido.
    status(400, post("/api/estudiantes", "{"));
  }

  // Pedir una actividad que no existe debe devolver 404 (Not Found).
  @Test
  void recursoInexistenteEs404() throws Exception {
    status(404, get("/api/actividades/99999999"));
  }
}
