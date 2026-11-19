// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Estudiante devuelto al cliente (el grupo se expone solo como id). */
public record EstudianteResponse(
    // Identificador único del estudiante.
    Long id,
    // Nombre de pila.
    String nombre,
    // Apellidos.
    String apellidos,
    // Email único.
    String email,
    // Fecha de nacimiento en formato YYYY-MM-DD.
    String fechaNacimiento,
    // Id del grupo al que pertenece.
    Long grupoId) {}
