// Paquete de los repositorios (acceso a datos).
package es.academia.campushub.repository;

// Entidad que gestiona este repositorio.
import es.academia.campushub.entity.Etiqueta;
// Interfaz base con las operaciones CRUD de JPA.
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de etiquetas.
 *
 * @author Marcel Abad
 */
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
  // Comprueba si ya existe una etiqueta con ese nombre, sin distinguir mayúsculas.
  boolean existsByNombreIgnoreCase(String nombre);
}
