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

@Service
@Transactional(readOnly = true)
public class ActividadDetalleServiceImpl implements ActividadDetalleService {

  private final ActividadRepository actividadRepository;
  private final InscripcionRepository inscripcionRepository;
  private final SeguimientoService seguimientoService;
  private final ActividadMapper actividadMapper;

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

  @Override
  public ActividadDetalleResponse detalle(Long id) {
    if (id == null || id <= 0) {
      throw new InvalidInputException("El identificador de actividad no es válido");
    }

    // 1. Consultar Actividad en PostgreSQL (404 si no existe)
    Actividad actividad =
        actividadRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("No existe la actividad con ID: " + id));

    // 2. Mapear actividad y docente responsable
    ActividadResponse actividadResponse = actividadMapper.toResponse(actividad);
    DocenteResponse responsableResponse = actividadMapper.toDocente(actividad.getDocente());

    // 3. Conteo de plazas en PostgreSQL (nunca negativo)
    int plazasOcupadas = (int) inscripcionRepository.countByActividadId(id);
    int plazasDisponibles = Math.max(0, actividad.getMaxParticipantes() - plazasOcupadas);

    // 4. Estudiantes inscritos desde PostgreSQL (lista vacía si no hay, nunca null)
    List<EstudianteResponse> estudiantes =
        inscripcionRepository.findByActividadIdConEstudiante(id).stream()
            .map(actividadMapper::toEstudiante)
            .toList();

    // 5. Etiquetas asociadas desde PostgreSQL (lista vacía si no hay, nunca null)
    List<EtiquetaResponse> etiquetas =
        actividad.getEtiquetas() == null
            ? List.of()
            : actividad.getEtiquetas().stream().map(actividadMapper::toResponse).toList();

    // 6. Seguimientos desde MongoDB (lista vacía si no hay, nunca null)
    List<SeguimientoResponse> seguimientos = seguimientoService.obtenerSeguimientos(id);
    if (seguimientos == null) {
      seguimientos = List.of();
    }

    // 7. Construir DTO de salida
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
