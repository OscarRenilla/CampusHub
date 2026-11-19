// Paquete de los repositorios (acceso a datos).
package es.academia.campushub.repository;

// Entidad que gestiona este repositorio.
import es.academia.campushub.entity.Estudiante;
// Interfaz base con las operaciones CRUD de JPA.
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de estudiantes.
 *
 * @author Álvaro Cotumba
 */
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
  // Comprueba si ya existe un estudiante con ese email, sin distinguir mayúsculas.
  boolean existsByEmailIgnoreCase(String email);
}
