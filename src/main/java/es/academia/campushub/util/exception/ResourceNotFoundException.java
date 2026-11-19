// Paquete de excepciones propias de la aplicación.
package es.academia.campushub.util.exception;

/**
 * Se lanza cuando un recurso buscado por id no existe (actividad, estudiante, etiqueta...).
 * ApiExceptionHandler la convierte en una respuesta HTTP 404 (Not Found).
 */
public class ResourceNotFoundException extends RuntimeException {
  // Recibe el mensaje que verá el cliente y lo pasa a la clase padre.
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
