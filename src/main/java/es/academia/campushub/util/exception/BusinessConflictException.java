// Paquete de excepciones propias de la aplicación.
package es.academia.campushub.util.exception;

/**
 * Se lanza cuando una regla de negocio se incumple (duplicados, aforo completo...).
 * ApiExceptionHandler la convierte en una respuesta HTTP 409 (Conflict).
 */
public class BusinessConflictException extends RuntimeException {
  // Recibe el mensaje que verá el cliente y lo pasa a la clase padre.
  public BusinessConflictException(String message) {
    super(message);
  }
}
