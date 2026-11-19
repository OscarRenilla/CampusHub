// Paquete de los repositorios (acceso a datos).
package es.academia.campushub.repository;

// Entidad que gestiona este repositorio.
import es.academia.campushub.entity.Actividad;
// Tipo de bloqueo de JPA.
import jakarta.persistence.LockModeType;
// Lista de resultados.
import java.util.List;
// Resultado que puede estar vacío.
import java.util.Optional;
// Interfaz base con las operaciones CRUD de JPA.
import org.springframework.data.jpa.repository.JpaRepository;
// Anotación para aplicar un bloqueo a una consulta.
import org.springframework.data.jpa.repository.Lock;
// Anotación para escribir consultas JPQL propias.
import org.springframework.data.jpa.repository.Query;
// Anotación para enlazar un parámetro con la consulta.
import org.springframework.data.repository.query.Param;

/**
 * Repositorio de actividades. Hereda el CRUD básico de JpaRepository.
 *
 * @author Marcel Abad
 */
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

  /**
   * Busca la actividad bloqueando su fila en PostgreSQL (SELECT ... FOR UPDATE). Dos inscripciones
   * simultáneas a la misma actividad se ponen en fila, así nunca se supera el aforo.
   */
  // Bloqueo pesimista de escritura: nadie más puede modificar la fila hasta que termine la transacción.
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  // Consulta JPQL que selecciona la actividad por su id.
  @Query("select a from Actividad a where a.id = :id")
  Optional<Actividad> findByIdParaActualizar(@Param("id") Long id);

  /** Actividades que tienen una etiqueta concreta, ordenadas por id. */
  // Spring Data deduce la consulta del nombre del método (etiquetas.id y orden por id).
  List<Actividad> findByEtiquetasIdOrderById(Long etiquetaId);
}
