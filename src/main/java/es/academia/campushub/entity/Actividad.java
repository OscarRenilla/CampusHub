// Paquete de las entidades JPA (tablas de PostgreSQL).
package es.academia.campushub.entity;

// Importa todas las anotaciones de JPA (@Entity, @Id, @Column, @ManyToOne...).
import jakarta.persistence.*;
// Tipo de fecha sin hora para la fecha prevista.
import java.time.LocalDate;
// Implementación de lista para las inscripciones.
import java.util.ArrayList;
// Implementación de conjunto sin duplicados para las etiquetas.
import java.util.HashSet;
// Interfaz de lista.
import java.util.List;
// Interfaz de conjunto.
import java.util.Set;
// Lombok: genera automáticamente los getters.
import lombok.Getter;
// Lombok: genera el constructor sin argumentos que exige JPA.
import lombok.NoArgsConstructor;
// Lombok: genera automáticamente los setters.
import lombok.Setter;
// Hibernate: permite definir restricciones CHECK en la tabla.
import org.hibernate.annotations.Check;

/**
 * Actividad organizada por un docente. La regla de que el máximo de participantes sea mayor que
 * cero se protege en la propia base de datos con un CHECK, no solo en la API.
 *
 * @author Marcel Abad
 */
// Indica que la clase es una entidad persistente.
@Entity
// Nombre de la tabla y un índice sobre docente_id para acelerar las consultas por docente.
@Table(
    name = "actividades",
    indexes = @Index(name = "idx_actividad_docente", columnList = "docente_id"))
// Restricción CHECK en la BD: el aforo máximo siempre debe ser mayor que cero.
@Check(name = "ck_actividad_max_participantes", constraints = "max_participantes > 0")
// Genera los getters de todos los campos.
@Getter
// Genera los setters de todos los campos.
@Setter
// Genera el constructor vacío necesario para JPA.
@NoArgsConstructor
public class Actividad {
  // Clave primaria de la tabla.
  @Id
  // El id lo genera la BD de forma autoincremental (IDENTITY).
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Título: obligatorio, hasta 200 caracteres.
  @Column(nullable = false, length = 200)
  private String titulo;

  // Descripción: obligatoria, de tipo text para permitir textos largos.
  @Column(nullable = false, columnDefinition = "text")
  private String descripcion;

  // Tipo de actividad: obligatorio, hasta 50 caracteres.
  @Column(nullable = false, length = 50)
  private String tipo;

  // Fecha prevista: obligatoria; la columna se llama fecha_prevista.
  @Column(name = "fecha_prevista", nullable = false)
  private LocalDate fechaPrevista;

  // Aforo máximo: obligatorio; la columna se llama max_participantes.
  @Column(name = "max_participantes", nullable = false)
  private Integer maxParticipantes;

  // Relación N:1 con Docente: muchas actividades por docente. LAZY = el docente se carga solo si se usa.
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  // Columna de clave foránea docente_id, obligatoria.
  @JoinColumn(name = "docente_id", nullable = false)
  private Docente docente;

  // Relación N:M con Etiqueta; esta entidad es la propietaria de la relación.
  @ManyToMany
  // Tabla intermedia actividad_etiqueta con sus dos claves foráneas.
  @JoinTable(
      name = "actividad_etiqueta",
      joinColumns = @JoinColumn(name = "actividad_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "etiqueta_id", nullable = false),
      // Una misma etiqueta no puede asociarse dos veces a la misma actividad.
      uniqueConstraints =
          @UniqueConstraint(
              name = "uk_actividad_etiqueta",
              columnNames = {"actividad_id", "etiqueta_id"}))
  // Se usa Set para evitar duplicados también en memoria.
  private Set<Etiqueta> etiquetas = new HashSet<>();

  /** Lado inverso de Inscripcion.actividad (el propietario es Inscripcion). */
  // mappedBy: la FK vive en la tabla inscripciones, no aquí.
  @OneToMany(mappedBy = "actividad")
  private List<Inscripcion> inscripciones = new ArrayList<>();
}
