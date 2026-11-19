// Paquete de la capa de servicios (lógica de negocio).
package es.academia.campushub.service;

// DTO que agrupa toda la información de una actividad.
import es.academia.campushub.controller.dto.ActividadDetalleResponse;

/**
 * Contrato del servicio que construye el detalle completo de una actividad.
 *
 * @author Óscar Renilla
 * @version 1.0
 */
public interface ActividadDetalleService {

  // Devuelve el detalle completo (actividad, docente, plazas, estudiantes, etiquetas y seguimientos).
  ActividadDetalleResponse detalle(Long id);
}
