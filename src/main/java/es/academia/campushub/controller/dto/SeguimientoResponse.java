// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

// Mapa clave-valor para devolver los datos libres del seguimiento.
import java.util.Map;

/** Seguimiento devuelto al cliente; procede de un documento de MongoDB. */
public record SeguimientoResponse(
    // Id del documento en MongoDB (cadena hexadecimal, no numérico).
    String id,
    // Id de la actividad (PostgreSQL) a la que pertenece.
    Long actividadId,
    // Tipo de seguimiento.
    String tipo,
    // Datos libres guardados en el seguimiento.
    Map<String, Object> datos,
    // Fecha y hora de creación (formato ISO).
    String creadoEn) {}
