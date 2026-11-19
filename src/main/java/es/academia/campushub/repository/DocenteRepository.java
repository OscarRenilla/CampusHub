// Paquete de los repositorios (acceso a datos).
package es.academia.campushub.repository;

// Entidad que gestiona este repositorio.
import es.academia.campushub.entity.Docente;
// Interfaz base con las operaciones CRUD de JPA.
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de docentes.
 *
 * @author Álvaro Cotumba
 */
public interface DocenteRepository extends JpaRepository<Docente, Long> {
  // Comprueba si ya existe un docente con ese email, sin distinguir mayúsculas.
  boolean existsByEmailIgnoreCase(String email);
}
