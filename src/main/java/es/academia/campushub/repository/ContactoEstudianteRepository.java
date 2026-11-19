// Paquete de los repositorios (acceso a datos).
package es.academia.campushub.repository;

// Entidad que gestiona este repositorio.
import es.academia.campushub.entity.ContactoEstudiante;
// Resultado que puede estar vacío.
import java.util.Optional;
// Interfaz base con las operaciones CRUD de JPA.
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de fichas de contacto.
 *
 * @author Álvaro Cotumba
 */
public interface ContactoEstudianteRepository extends JpaRepository<ContactoEstudiante, Long> {
  // Busca la ficha de un estudiante concreto (como mucho una gracias a la FK única).
  Optional<ContactoEstudiante> findByEstudianteId(Long estudianteId);
}
