package es.academia.campushub.document;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequimientos")
public class Seguimiento {

    @Id
    private String id;

    private Long actividadId;

    private String tipo;
    private Map<String, Object> datos;

    private LocalDateTime creadoEn;

    // Constructor vacio
    // Getters y setters
}