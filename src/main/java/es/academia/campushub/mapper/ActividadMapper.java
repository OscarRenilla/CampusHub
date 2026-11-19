// Paquete de los conversores entre entidades y DTO.
package es.academia.campushub.mapper;

// DTO de respuesta a los que se convierten las entidades.
import es.academia.campushub.controller.dto.*;
// Entidades JPA de origen.
import es.academia.campushub.entity.*;
// Marca la clase como componente de Spring para poder inyectarla.
import org.springframework.stereotype.Component;

/**
 * Conversión entidad -> DTO del bloque de actividades, etiquetas e inscripciones. Usar dentro de
 * una transacción (las relaciones son LAZY).
 *
 * @author Marcel Abad
 */
// Componente gestionado por Spring (singleton).
@Component
public class ActividadMapper {
  // Mapper de organización, reutilizado para convertir docentes y estudiantes.
  private final OrganizacionMapper organizacion;

  // Constructor: Spring inyecta el mapper de organización.
  public ActividadMapper(OrganizacionMapper organizacion) {
    this.organizacion = organizacion;
  }

  // Convierte una Actividad en su DTO de respuesta.
  public ActividadResponse toResponse(Actividad a) {
    return new ActividadResponse(
        // Id de la actividad.
        a.getId(),
        // Título.
        a.getTitulo(),
        // Descripción.
        a.getDescripcion(),
        // Tipo.
        a.getTipo(),
        // Fecha convertida a texto (YYYY-MM-DD).
        a.getFechaPrevista().toString(),
        // Aforo máximo.
        a.getMaxParticipantes(),
        // Solo el id del docente responsable (el DTO no incluye el docente completo).
        a.getDocente().getId());
  }

  // Convierte una Etiqueta en su DTO de respuesta.
  public EtiquetaResponse toResponse(Etiqueta e) {
    return new EtiquetaResponse(e.getId(), e.getNombre());
  }

  // Convierte una Inscripcion en su DTO de respuesta.
  public InscripcionResponse toResponse(Inscripcion i) {
    return new InscripcionResponse(
        // Id de la inscripción.
        i.getId(),
        // Id de la actividad.
        i.getActividad().getId(),
        // Id del estudiante.
        i.getEstudiante().getId(),
        // Fecha y hora convertidas a texto ISO.
        i.getFechaInscripcion().toString());
  }

  /** Elemento del historial de un estudiante (RF08). */
  public ActividadHistorialResponse toHistorial(Inscripcion i) {
    // Actividad asociada a la inscripción.
    Actividad a = i.getActividad();
    return new ActividadHistorialResponse(
        // Id de la actividad.
        a.getId(),
        // Título.
        a.getTitulo(),
        // Tipo.
        a.getTipo(),
        // Fecha prevista como texto.
        a.getFechaPrevista().toString(),
        // Fecha de inscripción como texto.
        i.getFechaInscripcion().toString(),
        // Docente responsable convertido a DTO.
        organizacion.toResponse(a.getDocente()));
  }

  // Extrae el estudiante de una inscripción y lo convierte a DTO.
  public EstudianteResponse toEstudiante(Inscripcion i) {
    return organizacion.toResponse(i.getEstudiante());
  }

  // Convierte un Docente en DTO delegando en el mapper de organización.
  public DocenteResponse toDocente(Docente d) {
    return organizacion.toResponse(d);
  }
}
