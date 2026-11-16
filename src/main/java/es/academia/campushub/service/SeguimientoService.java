package es.academia.campushub.service;

import es.academia.campushub.controller.dto.CreateSeguimientoRequest;
import es.academia.campushub.controller.dto.SeguimientoResponse;

import java.util.List;

/**
 * @author Óscar Renilla
 * @version 1.0
 */

/**
 * Interfaz que define las operaciones relacionadas con los seguimientos de las actividades de CampusHub.
 * <p>
 * Establece los métodos que debe implementar la clase SeguimientoServiceImpl.
 */
public interface SeguimientoService {

    /**
     * Registra un nuevo seguimiento para una actividad.
     *
     * @param actividadId identificador de la actividad
     * @param request     datos recibidos para crear el seguimiento
     * @return informacion del seguimiento registrado
     */
    SeguimientoResponse registrarSeguimiento(Long actividadId, CreateSeguimientoRequest request);

    /**
     * Obtiene todos los seguimientos asociados a una actividad.
     *
     * @param actividadId identificador de la actividad
     * @return lista de seguimientos de la actividad
     */
    List<SeguimientoResponse> obtenerSeguimientos(Long actividadId);
}
