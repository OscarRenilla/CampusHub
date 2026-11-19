// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Cuerpo JSON de PUT /api/estudiantes/{id}/contacto: crea la ficha si no existe o la actualiza. */
public record UpsertContactoRequest(
    // Nombre del tutor legal (obligatorio).
    String tutorLegal,
    // Teléfono (obligatorio, máx. 30 caracteres).
    String telefono,
    // Email del tutor (obligatorio, formato válido).
    String email,
    // Si el tutor autoriza la participación (obligatorio).
    Boolean autorizado,
    // Fecha de autorización YYYY-MM-DD; opcional.
    String fechaAutorizacion) {}
