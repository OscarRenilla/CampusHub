// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Docente devuelto al cliente (el departamento se expone solo como id). */
public record DocenteResponse(
    // Identificador único del docente.
    Long id,
    // Nombre de pila.
    String nombre,
    // Apellidos.
    String apellidos,
    // Email único.
    String email,
    // Id del departamento al que pertenece.
    Long departamentoId) {}
