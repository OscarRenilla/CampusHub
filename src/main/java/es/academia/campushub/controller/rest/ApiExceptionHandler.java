// Paquete de los controladores REST.
package es.academia.campushub.controller.rest;

// Excepciones propias de la aplicación.
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.PersistenciaPendienteException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
// Para añadir la marca de tiempo al cuerpo de error.
import java.time.Instant;
// Mapa inmutable que forma el JSON de error.
import java.util.Map;
// Excepción de Spring que salta cuando la BD rechaza una operación (UNIQUE, FK, CHECK...).
import org.springframework.dao.DataIntegrityViolationException;
// Códigos de estado HTTP.
import org.springframework.http.HttpStatus;
// Respuesta HTTP con estado y cuerpo.
import org.springframework.http.ResponseEntity;
// Excepción de Spring cuando el JSON de la petición no se puede leer.
import org.springframework.http.converter.HttpMessageNotReadableException;
// Anotación para declarar qué método gestiona cada tipo de excepción.
import org.springframework.web.bind.annotation.ExceptionHandler;
// Anotación que aplica este manejador a todos los controladores REST.
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de errores: traduce las excepciones de la aplicación a respuestas HTTP con un
 * JSON uniforme (code, message, timestamp). Las reglas de integridad siguen siendo
 * responsabilidad del alumno.
 */
// Intercepta las excepciones lanzadas por cualquier controlador.
@RestControllerAdvice
public class ApiExceptionHandler {
  // Operación pendiente de implementar -> HTTP 501.
  @ExceptionHandler(PersistenciaPendienteException.class)
  public ResponseEntity<Map<String, Object>> pending(PersistenciaPendienteException ex) {
    return response(HttpStatus.NOT_IMPLEMENTED, "NOT_IMPLEMENTED", ex.getMessage());
  }

  // Violación de una restricción de la BD -> HTTP 409.
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, Object>> integrity(DataIntegrityViolationException ex) {
    // No devolver SQL ni detalles internos al cliente.
    return response(HttpStatus.CONFLICT, "DATA_INTEGRITY", "Restricción de integridad incumplida");
  }

  // JSON mal formado o ilegible -> HTTP 400.
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> malformed(HttpMessageNotReadableException ex) {
    return response(HttpStatus.BAD_REQUEST, "BAD_JSON", "El cuerpo JSON no es válido");
  }

  // Recurso inexistente -> HTTP 404.
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> missing(ResourceNotFoundException ex) {
    return response(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
  }

  // Regla de negocio incumplida (duplicado, aforo completo...) -> HTTP 409.
  @ExceptionHandler(BusinessConflictException.class)
  public ResponseEntity<Map<String, Object>> conflict(BusinessConflictException ex) {
    return response(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage());
  }

  // Datos de entrada inválidos -> HTTP 400.
  @ExceptionHandler(InvalidInputException.class)
  public ResponseEntity<Map<String, Object>> invalid(InvalidInputException ex) {
    return response(HttpStatus.BAD_REQUEST, "INVALID_INPUT", ex.getMessage());
  }

  // Construye la respuesta de error común: estado HTTP + código, mensaje y fecha/hora.
  private ResponseEntity<Map<String, Object>> response(
      HttpStatus status, String code, String message) {
    return ResponseEntity.status(status)
        .body(Map.of("code", code, "message", message, "timestamp", Instant.now().toString()));
  }
}
