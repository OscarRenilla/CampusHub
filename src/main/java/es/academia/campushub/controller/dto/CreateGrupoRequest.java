// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Cuerpo JSON recibido en POST /api/grupos. */
public record CreateGrupoRequest(
    // Nombre del grupo (por ejemplo "DAM2-B").
    String nombre,
    // Nivel educativo (por ejemplo "2 DAM").
    String nivel,
    // Id del docente que actúa como tutor; debe existir.
    Long tutorDocenteId) {}
