// Paquete de los repositorios (acceso a datos).
package es.academia.campushub.repository;

// Entidad que gestiona este repositorio.
import es.academia.campushub.entity.Inscripcion;
// Lista de resultados.
import java.util.List;
// Interfaz base con las operaciones CRUD de JPA.
import org.springframework.data.jpa.repository.JpaRepository;
// Anotación para escribir consultas JPQL propias.
import org.springframework.data.jpa.repository.Query;
// Anotación para enlazar un parámetro con la consulta.
import org.springframework.data.repository.query.Param;

/**
 * Repositorio de inscripciones.
 *
 * @author Marcel Abad
 */
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

  // Comprueba si el estudiante ya está inscrito en la actividad (evita duplicados).
  boolean existsByActividadIdAndEstudianteId(Long actividadId, Long estudianteId);

  /** Plazas ocupadas de una actividad. */
  // Cuenta las inscripciones de la actividad.
  long countByActividadId(Long actividadId);

  /** Inscripciones de una actividad con su estudiante ya cargado (evita consultas extra). */
  // join fetch carga el estudiante en la misma consulta (evita el problema N+1).
  @Query(
      "select i from Inscripcion i join fetch i.estudiante "
          + "where i.actividad.id = :actividadId order by i.id")
  List<Inscripcion> findByActividadIdConEstudiante(@Param("actividadId") Long actividadId);

  /** Historial de un estudiante: inscripciones con su actividad y el docente responsable. */
  // Carga de golpe inscripción, actividad y docente; ordena por fecha prevista y luego por id.
  @Query(
      "select i from Inscripcion i join fetch i.actividad a join fetch a.docente "
          + "where i.estudiante.id = :estudianteId order by a.fechaPrevista, i.id")
  List<Inscripcion> findHistorialByEstudianteId(@Param("estudianteId") Long estudianteId);
}
