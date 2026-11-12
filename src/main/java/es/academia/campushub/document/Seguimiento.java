package es.academia.campushub.document;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Seguimiento de una actividad almacenado en MongoDB.
 *
 * <p>La actividad se referencia mediante su identificador de PostgreSQL. El campo {@code datos}
 * conserva una estructura flexible de mapas, listas y valores JSON.
 */

@Document(collection = "seguimientos")
public class Seguimiento {

    @Id
    private String id;

    private Long actividadId;

    private String tipo;

    private Map<String, Object> datos;

    private LocalDateTime creadoEn;

    public Seguimiento() {
    }

    public Seguimiento(
            String id, Long actividadId, String tipo, Map<String, Object> datos, LocalDateTime creadoEn) {
        this.id = id;
        this.actividadId = actividadId;
        this.tipo = tipo;
        this.datos = datos;
        this.creadoEn = creadoEn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getActividadId() {
        return actividadId;
    }

    public void setActividadId(Long actividadId) {
        this.actividadId = actividadId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Map<String, Object> getDatos() {
        return datos;
    }

    public void setDatos(Map<String, Object> datos) {
        this.datos = datos;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;

    }
}