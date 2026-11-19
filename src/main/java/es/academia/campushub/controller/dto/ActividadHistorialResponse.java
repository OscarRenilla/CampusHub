// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/**
 * Elemento del historial de actividades de un estudiante
 * (GET /api/estudiantes/{id}/actividades). Una fila por cada inscripción.
 */
public record ActividadHistorialResponse(
    // Identificador de la actividad.
    Long id,
    // Título de la actividad.
    String titulo,
    // Tipo de actividad (TALLER, CHARLA, VISITA...).
    String tipo,
    // Fecha prevista de la actividad en formato YYYY-MM-DD.
    String fechaPrevista,
    // Fecha y hora en que el estudiante se inscribió (formato ISO).
    String fechaInscripcion,
    // Docente responsable de la actividad.
    DocenteResponse responsable) {}
