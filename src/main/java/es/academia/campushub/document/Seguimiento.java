package es.academia.campushub.document;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Óscar Renilla
 * @version 1.0
 */

/**
 * Representa un seguimiento de una actividad almacenado en MongoDB.
 * <p>
 * Cada documento contiene el identificador de la actividad relacionada, el tipo de seguimiento, sus datos y la fecha de creación.
 * <p>
 * La actividad se identifica mediante su ID de PostgreSQL, sin necesidad de almacenar toda la entidad Actividad en MongoDB.
 */

/**
 * Seguimiento de una actividad almacenado en MongoDB.
 *
 * <p>La actividad se referencia mediante su identificador de PostgreSQL. El campo {@code datos}
 * conserva una estructura flexible de mapas, listas y valores JSON.
 */
@Document(collection = "seguimientos")
public class Seguimiento {

    // Identificador único de la actividad generado por MongoDB
    @Id
    private String id;

    // Identificador de la actividad de PostgreSQL a la que pertenece el seguimiento.
    private Long actividadId;

    // Tipo de seguimiento que se está registrando.
    private String tipo;

    // Datos adicionales del seguimiento almacenados en formato clave-valor.
    // Se utiliza Object para permitir diferentes tipos de datos.
    private Map<String, Object> datos;

    // Fecha y hora en la que se creó el seguimiento.
    private LocalDateTime creadoEn;

    // Constructor vacío necesario para que Spring Data MongoDB pueda crear instancias de esta clase.
    public Seguimiento() {
    }

    /**
     * Constructor que permite inicializar todos los atributos del seguimiento.
     * @param id identificador del documento
     * @param actividadId identificador de la actividad relacionada
     * @param tipo tipo de seguimiento
     * @param datos informacion adicional del seguimiento
     * @param creadoEn fecha y hora de creación
     */
    public Seguimiento(
            String id, Long actividadId, String tipo, Map<String, Object> datos, LocalDateTime creadoEn) {
        this.id = id;
        this.actividadId = actividadId;
        this.tipo = tipo;
        this.datos = datos;
        this.creadoEn = creadoEn;
    }

    // Devuelve el identificador del seguimiento.
    public String getId() {
        return id;
    }

    // Modifica el identificador del seguimiento.
    public void setId(String id) {
        this.id = id;
    }

    // Devuelve el identificador de la actividad relacionada.
    public Long getActividadId() {
        return actividadId;
    }

    // Establece la actividad a la que pertenece el seguimiento.
    public void setActividadId(Long actividadId) {
        this.actividadId = actividadId;
    }

    // Devuelve el tipo de seguimiento.
    public String getTipo() {
        return tipo;
    }

    // Modifica el tipo de seguimiento.
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // Devuelve los datos adicionales del seguimiento.
    public Map<String, Object> getDatos() {
        return datos;
    }

    // Establece los datos adicionales del seguimiento.
    public void setDatos(Map<String, Object> datos) {
        this.datos = datos;
    }

    // Devuelve la fecha y hora de creación.
    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    // Establece la fecha y hora de creación.
    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}