// Paquete de los repositorios (acceso a datos).
package es.academia.campushub.repository;

// Entidad que gestiona este repositorio.
import es.academia.campushub.entity.Departamento;
// Interfaz base con las operaciones CRUD de JPA.
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de departamentos.
 *
 * @author Álvaro Cotumba
 */
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
  // Comprueba si ya existe un departamento con ese nombre, sin distinguir mayúsculas.
  boolean existsByNombreIgnoreCase(String nombre);
}
