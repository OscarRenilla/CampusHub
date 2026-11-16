package es.academia.campushub.repository;

import es.academia.campushub.document.Seguimiento;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Óscar Renilla
 * @version 1.0
 */

/**
 * Acceso a los seguimientos almacenados en MongoDB
 */

/**
 * Repositorio encargado de acceder a los documentos Seguimiento
 * almacenados en la colección seguimientos de MongoDB.
 * Hereda de MongoRepository las operaciones CRUD básicas,
 * como guardar, buscar, actualizar y eliminar documentos.
 */
public interface SeguimientoRepository extends MongoRepository<Seguimiento, String> {

    /**
     * Busca todos los seguimientos asociados a una actividad.
     * <p>
     * Spring Data MongoDB genera automaticamente la consulta a partir del nombre del método.
     *
     * @param actividadId identificador de la actividad
     * @return lista de seguimientos encontrados
     */
    List<Seguimiento> findByActividadId(Long actividadId);

    /**
     * Busca los seguimientos de una actividad y los ordena por fecha de creación de forma descendente (más recientes primero).
     *
     * @param actividadId identificador de la actividad
     * @return lista de seguimientos ordenados por fecha
     */
    List<Seguimiento> findByActividadIdOrderByCreadoEnDesc(Long actividadId);
}
