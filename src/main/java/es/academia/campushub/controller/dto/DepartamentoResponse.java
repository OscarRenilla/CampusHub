// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Departamento devuelto al cliente: su id y su nombre. */
public record DepartamentoResponse(Long id, String nombre) {}
