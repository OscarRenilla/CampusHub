// Paquete de la capa de servicios (lógica de negocio).
package es.academia.campushub.service;

// DTO de entrada y salida de los seguimientos.
import es.academia.campushub.controller.dto.CreateSeguimientoRequest;
import es.academia.campushub.controller.dto.SeguimientoResponse;

// Interfaz de lista.
import java.util.List;

/**
 * Interfaz que define las operaciones relacionadas con los seguimientos de las actividades de
 * CampusHub. Establece los métodos que debe implementar la clase SeguimientoServiceImpl.
 *
 * @author Óscar Renilla
 * @version 1.0
 */
public interface SeguimientoService {

    /**
     * Registra un nuevo seguimiento para una actividad.
     *
     * @param actividadId identificador de la actividad
     * @param request     datos recibidos para crear el seguimiento
     * @return información del seguimiento registrado
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
