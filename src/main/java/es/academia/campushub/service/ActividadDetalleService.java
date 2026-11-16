package es.academia.campushub.service;

import es.academia.campushub.controller.dto.ActividadDetalleResponse;

/**
 * @author Óscar Renilla
 * @version 1.0
 */
public interface ActividadDetalleService {

  ActividadDetalleResponse detalle(Long id);
}
