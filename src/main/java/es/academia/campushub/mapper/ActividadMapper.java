package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.entity.*;
import org.springframework.stereotype.Component;

/**
 * Conversión entidad -> DTO del bloque de actividades, etiquetas e inscripciones. Usar dentro de
 * una transacción (las relaciones son LAZY).
 */
@Component
public class ActividadMapper {
  private final OrganizacionMapper organizacion;

  public ActividadMapper(OrganizacionMapper organizacion) {
    this.organizacion = organizacion;
  }

  public ActividadResponse toResponse(Actividad a) {
    return new ActividadResponse(
        a.getId(),
        a.getTitulo(),
        a.getDescripcion(),
        a.getTipo(),
        a.getFechaPrevista().toString(),
        a.getMaxParticipantes(),
        a.getDocente().getId());
  }

  public EtiquetaResponse toResponse(Etiqueta e) {
    return new EtiquetaResponse(e.getId(), e.getNombre());
  }

  public InscripcionResponse toResponse(Inscripcion i) {
    return new InscripcionResponse(
        i.getId(),
        i.getActividad().getId(),
        i.getEstudiante().getId(),
        i.getFechaInscripcion().toString());
  }

  /** Elemento del historial de un estudiante (RF08). */
  public ActividadHistorialResponse toHistorial(Inscripcion i) {
    Actividad a = i.getActividad();
    return new ActividadHistorialResponse(
        a.getId(),
        a.getTitulo(),
        a.getTipo(),
        a.getFechaPrevista().toString(),
        i.getFechaInscripcion().toString(),
        organizacion.toResponse(a.getDocente()));
  }

  public EstudianteResponse toEstudiante(Inscripcion i) {
    return organizacion.toResponse(i.getEstudiante());
  }

  public DocenteResponse toDocente(Docente d) {
    return organizacion.toResponse(d);
  }
}
