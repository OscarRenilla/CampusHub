package es.academia.campushub.repository;

import es.academia.campushub.entity.Actividad;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {

  /**
   * Busca la actividad bloqueando su fila en PostgreSQL (SELECT ... FOR UPDATE). Dos inscripciones
   * simultáneas a la misma actividad se ponen "en fila", así nunca se supera el aforo.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from Actividad a where a.id = :id")
  Optional<Actividad> findByIdParaActualizar(@Param("id") Long id);

  /** Actividades que tienen una etiqueta concreta. */
  List<Actividad> findByEtiquetasIdOrderById(Long etiquetaId);
}
