// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Cuerpo JSON recibido en POST /api/departamentos. El nombre debe ser único. */
public record CreateDepartamentoRequest(String nombre) {}
