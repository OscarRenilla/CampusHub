// Paquete de los documentos de MongoDB.
package es.academia.campushub.document;

// Tipo de fecha con hora para el momento de creación.
import java.time.LocalDateTime;
// Mapa clave-valor para los datos de estructura libre.
import java.util.Map;

// Marca el campo que actúa como identificador del documento.
import org.springframework.data.annotation.Id;
// Indica que la clase se guarda como documento en una colección de MongoDB.
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Seguimiento de una actividad almacenado en MongoDB.
 * <p>
 * Cada documento contiene el identificador de la actividad relacionada, el tipo de seguimiento,
 * sus datos y la fecha de creación. La actividad se referencia mediante su ID de PostgreSQL, sin
 * necesidad de almacenar toda la entidad Actividad en MongoDB. El campo datos conserva una
 * estructura flexible de mapas, listas y valores JSON.
 *
 * @author Óscar Renilla
 * @version 1.0
 */
// Los objetos de esta clase se guardan en la colección seguimientos de MongoDB.
@Document(collection = "seguimientos")
public class Seguimiento {

    // Identificador único del documento, generado por MongoDB (cadena hexadecimal).
    @Id
    private String id;

    // Identificador de la actividad de PostgreSQL a la que pertenece el seguimiento.
    private Long actividadId;

    // Tipo de seguimiento que se está registrando.
    private String tipo;

    // Datos adicionales del seguimiento en formato clave-valor.
    // Se utiliza Object para permitir diferentes tipos de datos (textos, números, listas, mapas...).
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
     * @param datos información adicional del seguimiento
     * @param creadoEn fecha y hora de creación
     */
    public Seguimiento(
            String id, Long actividadId, String tipo, Map<String, Object> datos, LocalDateTime creadoEn) {
        // Guarda el id recibido en el atributo de la clase.
        this.id = id;
        // Guarda la actividad relacionada.
        this.actividadId = actividadId;
        // Guarda el tipo de seguimiento.
        this.tipo = tipo;
        // Guarda los datos adicionales.
        this.datos = datos;
        // Guarda la fecha de creación.
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
