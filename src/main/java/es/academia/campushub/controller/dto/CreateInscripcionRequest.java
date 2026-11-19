// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Cuerpo JSON recibido en POST /api/actividades/{id}/inscripciones: solo indica qué estudiante se inscribe. */
public record CreateInscripcionRequest(Long estudianteId) {}
