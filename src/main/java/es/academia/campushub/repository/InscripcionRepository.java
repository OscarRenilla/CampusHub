package es.academia.campushub.repository;

import es.academia.campushub.entity.Inscripcion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

  boolean existsByActividadIdAndEstudianteId(Long actividadId, Long estudianteId);

  /** Plazas ocupadas de una actividad. */
  long countByActividadId(Long actividadId);

  /** Inscripciones de una actividad con su estudiante ya cargado (evita consultas extra). */
  @Query(
      "select i from Inscripcion i join fetch i.estudiante "
          + "where i.actividad.id = :actividadId order by i.id")
  List<Inscripcion> findByActividadIdConEstudiante(@Param("actividadId") Long actividadId);

  /** Historial de un estudiante: inscripciones con su actividad y el docente responsable. */
  @Query(
      "select i from Inscripcion i join fetch i.actividad a join fetch a.docente "
          + "where i.estudiante.id = :estudianteId order by a.fechaPrevista, i.id")
  List<Inscripcion> findHistorialByEstudianteId(@Param("estudianteId") Long estudianteId);
}
