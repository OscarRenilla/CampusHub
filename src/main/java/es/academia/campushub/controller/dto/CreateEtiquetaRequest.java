// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Cuerpo JSON recibido en POST /api/etiquetas. El nombre debe ser único. */
public record CreateEtiquetaRequest(String nombre) {}
