// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Datos de una actividad que se devuelven al cliente. */
public record ActividadResponse(
    // Identificador único de la actividad.
    Long id,
    // Título corto de la actividad.
    String titulo,
    // Descripción detallada.
    String descripcion,
    // Tipo de actividad (TALLER, CHARLA, VISITA...).
    String tipo,
    // Fecha prevista en formato YYYY-MM-DD.
    String fechaPrevista,
    // Aforo máximo de participantes.
    Integer maxParticipantes,
    // Identificador del docente responsable.
    Long docenteId) {}
