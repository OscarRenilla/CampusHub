// Paquete de los repositorios (acceso a datos).
package es.academia.campushub.repository;

// Documento de MongoDB que gestiona este repositorio.
import es.academia.campushub.document.Seguimiento;

// Lista de resultados.
import java.util.List;

// Interfaz base con las operaciones CRUD de MongoDB.
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositorio encargado de acceder a los documentos Seguimiento almacenados en la colección
 * seguimientos de MongoDB. Hereda de MongoRepository las operaciones CRUD básicas (guardar,
 * buscar, actualizar y eliminar).
 *
 * @author Óscar Renilla
 * @version 1.0
 */
public interface SeguimientoRepository extends MongoRepository<Seguimiento, String> {

    /**
     * Busca todos los seguimientos asociados a una actividad.
     * <p>
     * Spring Data MongoDB genera automáticamente la consulta a partir del nombre del método.
     *
     * @param actividadId identificador de la actividad
     * @return lista de seguimientos encontrados
     */
    List<Seguimiento> findByActividadId(Long actividadId);

    /**
     * Busca los seguimientos de una actividad y los ordena por fecha de creación de forma
     * descendente (más recientes primero).
     *
     * @param actividadId identificador de la actividad
     * @return lista de seguimientos ordenados por fecha
     */
    List<Seguimiento> findByActividadIdOrderByCreadoEnDesc(Long actividadId);
}
