// Paquete de los DTO (objetos de transferencia de datos) que viajan por la API REST.
package es.academia.campushub.controller.dto;

// Necesario para devolver listas de estudiantes, etiquetas y seguimientos.
import java.util.List;

/**
 * Respuesta del endpoint GET /api/actividades/{id}/detalle.
 * <p>
 * Agrupa en un solo objeto datos de PostgreSQL (actividad, docente, plazas, estudiantes,
 * etiquetas) y de MongoDB (seguimientos). Lo construye ActividadDetalleServiceImpl.
 *
 * @author Óscar Renilla
 */
public record ActividadDetalleResponse(
    // Datos básicos de la actividad (título, fecha, aforo máximo...).
    ActividadResponse actividad,
    // Docente responsable de la actividad.
    DocenteResponse responsable,
    // Número de plazas ya ocupadas por inscripciones.
    Integer plazasOcupadas,
    // Plazas que quedan libres (máximo - ocupadas, nunca negativo).
    Integer plazasDisponibles,
    // Estudiantes inscritos en la actividad.
    List<EstudianteResponse> estudiantes,
    // Etiquetas asociadas a la actividad.
    List<EtiquetaResponse> etiquetas,
    // Seguimientos de la actividad guardados en MongoDB (más recientes primero).
    List<SeguimientoResponse> seguimientos) {}
