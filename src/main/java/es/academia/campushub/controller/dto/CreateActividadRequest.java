// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Cuerpo JSON recibido en POST /api/actividades para crear una actividad. */
public record CreateActividadRequest(
    // Título de la actividad (obligatorio, máx. 200 caracteres).
    String titulo,
    // Descripción (obligatoria).
    String descripcion,
    // Tipo de actividad (obligatorio, máx. 50 caracteres).
    String tipo,
    // Fecha prevista en formato YYYY-MM-DD (obligatoria).
    String fechaPrevista,
    // Aforo máximo; debe ser mayor que cero.
    Integer maxParticipantes,
    // Id del docente responsable; debe existir.
    Long docenteId) {}
