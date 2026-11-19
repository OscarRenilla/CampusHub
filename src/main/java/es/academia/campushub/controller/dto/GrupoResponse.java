// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Grupo devuelto al cliente (el tutor se expone solo como id de docente). */
public record GrupoResponse(Long id, String nombre, String nivel, Long tutorDocenteId) {}
