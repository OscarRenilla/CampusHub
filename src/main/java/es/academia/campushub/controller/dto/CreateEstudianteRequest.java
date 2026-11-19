// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Cuerpo JSON recibido en POST /api/estudiantes. */
public record CreateEstudianteRequest(
    // Nombre de pila del estudiante.
    String nombre,
    // Apellidos del estudiante.
    String apellidos,
    // Email (formato válido y único).
    String email,
    // Fecha de nacimiento en formato YYYY-MM-DD; no puede estar en el futuro.
    String fechaNacimiento,
    // Id del grupo al que pertenece; debe existir.
    Long grupoId) {}
