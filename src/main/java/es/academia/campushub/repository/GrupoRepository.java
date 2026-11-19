// Paquete de los repositorios (acceso a datos).
package es.academia.campushub.repository;

// Entidad que gestiona este repositorio.
import es.academia.campushub.entity.Grupo;
// Interfaz base con las operaciones CRUD de JPA.
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de grupos. Solo necesita el CRUD básico heredado.
 *
 * @author Álvaro Cotumba
 */
public interface GrupoRepository extends JpaRepository<Grupo, Long> {}
