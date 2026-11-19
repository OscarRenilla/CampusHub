// Paquete de excepciones propias de la aplicación.
package es.academia.campushub.util.exception;

/**
 * Se lanza cuando los datos de entrada son inválidos (campos vacíos, formatos erróneos...).
 * ApiExceptionHandler la convierte en una respuesta HTTP 400 (Bad Request).
 */
public class InvalidInputException extends RuntimeException {
  // Recibe el mensaje que verá el cliente y lo pasa a la clase padre.
  public InvalidInputException(String message) {
    super(message);
  }
}
