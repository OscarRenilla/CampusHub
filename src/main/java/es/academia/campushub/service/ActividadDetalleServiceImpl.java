package es.academia.campushub.service;

import es.academia.campushub.controller.dto.ActividadDetalleResponse;
import es.academia.campushub.controller.dto.ActividadResponse;
import es.academia.campushub.controller.dto.DocenteResponse;
import es.academia.campushub.controller.dto.EstudianteResponse;
import es.academia.campushub.controller.dto.EtiquetaResponse;
import es.academia.campushub.controller.dto.SeguimientoResponse;
import es.academia.campushub.entity.Actividad;
import es.academia.campushub.mapper.ActividadMapper;
import es.academia.campushub.repository.ActividadRepository;
import es.academia.campushub.repository.InscripcionRepository;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.ResourceNotFoundException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Óscar Renilla
 * @version 1.0
 */

/**
 * Implementación del servicio encargado de obtener el detalle completo de una actividad.
 * <p>
 * Combina información procedente de PostgreSQL y MongoDB para construir
 * una respuesta con los datos de la actividad, el docente responsable,
 * las plazas, los estudiantes inscritos, las etiquetas y los seguimientos.
 */
@Service
// Todas las operaciones de esta clase se ejecutan en modo solo lectura,
// ya que su funcion es consultar informacion y no modificarla.
@Transactional(readOnly = true)
public class ActividadDetalleServiceImpl implements ActividadDetalleService {

    // Repositorio que permite consultar las actividades en PostgreSQL.
    private final ActividadRepository actividadRepository;

    // Repositorio que permite consultar las inscripciones de una actividad.
    private final InscripcionRepository inscripcionRepository;

    // Servicio encargado de recuperar los seguimientos desde MongoDB.
    private final SeguimientoService seguimientoService;

    // Mapper que transforma las entidades en sus correspondientes DTO.
    private final ActividadMapper actividadMapper;


    /**
     * Constructor que recibe las dependencias necesarias mediante
     * inyección de dependencias de Spring.
     *
     * @param actividadRepository   repositorio de actividades
     * @param inscripcionRepository repositorio de inscripciones
     * @param seguimientoService    servicio de seguimientos
     * @param actividadMapper       conversor de entidades a DTO
     */
    public ActividadDetalleServiceImpl(
            ActividadRepository actividadRepository,
            InscripcionRepository inscripcionRepository,
            SeguimientoService seguimientoService,
            ActividadMapper actividadMapper) {
        this.actividadRepository = actividadRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.seguimientoService = seguimientoService;
        this.actividadMapper = actividadMapper;
    }

    /**
     * Obtiene toda la información relacionada con una actividad.
     * <p>
     * Comprueba que el identificador sea válido y que la actividad exista.
     * Después recupera sus datos relacionados y construye un DTO con toda la información necesaria.
     *
     * @param id identificador de la actividad
     * @return detalle completo de la actividad
     */
    @Override
    public ActividadDetalleResponse detalle(Long id) {

        // Comprobar que el identificador recibido sea válido
        if (id == null || id <= 0) {
            throw new InvalidInputException("El identificador de actividad no es válido");
        }

        // 1. Consultar la actividad en PostgreSQL.
        // Si no existe, se lanza una excepción de recurso no encontrado (404).
         Actividad actividad =
                actividadRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("No existe la actividad con ID: " + id));

        // 2. Convertir la actividad y su docente responsable en DTO.
        // El mapper se encarga de transformar las entidades en objetos de respuesta.
        ActividadResponse actividadResponse = actividadMapper.toResponse(actividad);
        DocenteResponse responsableResponse = actividadMapper.toDocente(actividad.getDocente());

        // 3. Consultar el número de inscripciones de la actividad en PostgreSQL.
        int plazasOcupadas = (int) inscripcionRepository.countByActividadId(id);

        // Calcular las plazas disponibles, evitando que el resultado sea negativo.
        int plazasDisponibles = Math.max(0, actividad.getMaxParticipantes() - plazasOcupadas);

        // 4. Recuperar los estudiantes inscritos en la actividad.
        // Se convierten las entidades obtenidas en DTO de estudiante.
        // Si no hay inscripciones, la lista resultante estará vacía.
        List<EstudianteResponse> estudiantes =
                inscripcionRepository.findByActividadIdConEstudiante(id).stream()
                        .map(actividadMapper::toEstudiante)
                        .toList();

        // 5. Obtener las etiquetas asociadas a la actividad.
        // Si la actividad no tiene etiquetas, se devuelve una lista vacía.
        List<EtiquetaResponse> etiquetas =
                actividad.getEtiquetas() == null
                        ? List.of()
                        : actividad.getEtiquetas().stream().map(actividadMapper::toResponse).toList();

        // 6. Recuperar los seguimientos asociados a la actividad desde MongoDB.
        // Si el servicio devuelve null, se sustituye por una lista vacía.
        List<SeguimientoResponse> seguimientos = seguimientoService.obtenerSeguimientos(id);
        if (seguimientos == null) {
            seguimientos = List.of();
        }

        // 7. Construir y devolver el DTO con toda la información recopilada.
         return new ActividadDetalleResponse(
                actividadResponse,
                responsableResponse,
                plazasOcupadas,
                plazasDisponibles,
                estudiantes,
                etiquetas,
                seguimientos);
    }
}
