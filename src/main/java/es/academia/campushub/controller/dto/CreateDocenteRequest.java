// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Cuerpo JSON recibido en POST /api/docentes. El email debe ser único y el departamento existir. */
public record CreateDocenteRequest(
    // Nombre de pila del docente.
    String nombre,
    // Apellidos del docente.
    String apellidos,
    // Email del docente (formato válido y único).
    String email,
    // Id del departamento al que pertenece.
    Long departamentoId) {}
