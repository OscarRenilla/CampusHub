// Paquete de excepciones propias de la aplicación.
package es.academia.campushub.util.exception;

/**
 * Se usaba en los stubs de la plantilla inicial para indicar que una operación aún no estaba
 * implementada. ApiExceptionHandler la convierte en HTTP 501 (Not Implemented).
 */
public class PersistenciaPendienteException extends RuntimeException {
  // Recibe el nombre de la operación pendiente y construye un mensaje descriptivo.
  public PersistenciaPendienteException(String operacion) {
    super("Falta implementar persistencia: " + operacion);
  }
}
