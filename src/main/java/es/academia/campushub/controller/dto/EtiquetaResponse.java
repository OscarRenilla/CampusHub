// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

/** Etiqueta devuelta al cliente: su id y su nombre. */
public record EtiquetaResponse(Long id, String nombre) {}
