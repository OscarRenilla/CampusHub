// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Ficha de contacto de un estudiante que se devuelve al cliente. */
public record ContactoResponse(
    // Identificador del estudiante al que pertenece la ficha.
    Long estudianteId,
    // Nombre del tutor legal.
    String tutorLegal,
    // Teléfono de contacto.
    String telefono,
    // Email de contacto del tutor.
    String email,
    // Indica si el tutor autoriza al estudiante a participar en actividades.
    Boolean autorizado,
    // Fecha de la autorización (YYYY-MM-DD); puede ser null.
    String fechaAutorizacion) {}
