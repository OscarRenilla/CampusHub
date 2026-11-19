// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Inscripción devuelta al cliente: qué estudiante se apuntó a qué actividad y cuándo. */
public record InscripcionResponse(
    // Identificador de la inscripción.
    Long id,
    // Actividad en la que se inscribió.
    Long actividadId,
    // Estudiante inscrito.
    Long estudianteId,
    // Fecha y hora de la inscripción (formato ISO).
    String fechaInscripcion) {}
