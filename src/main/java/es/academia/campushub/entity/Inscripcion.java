// Paquete de las entidades JPA (tablas de PostgreSQL).
package es.academia.campushub.entity;

// Importa todas las anotaciones de JPA.
import jakarta.persistence.*;
// Tipo de fecha con hora para el momento de la inscripción.
import java.time.LocalDateTime;
// Lombok: genera los getters.
import lombok.Getter;
// Lombok: genera el constructor vacío que exige JPA.
import lombok.NoArgsConstructor;
// Lombok: genera los setters.
import lombok.Setter;

/**
 * Inscripción de un estudiante en una actividad.
 *
 * <p>La base de datos impide duplicados con UNIQUE(actividad_id, estudiante_id) y exige que ambas
 * claves foráneas existan. El índice por estudiante acelera la consulta del historial.
 *
 * @author Marcel Abad
 */
// Entidad persistente.
@Entity
// Tabla inscripciones con restricción única (actividad, estudiante) y un índice por estudiante.
@Table(
    name = "inscripciones",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_inscripcion_actividad_estudiante",
            columnNames = {"actividad_id", "estudiante_id"}),
    indexes = @Index(name = "idx_inscripcion_estudiante", columnList = "estudiante_id"))
// Genera getters.
@Getter
// Genera setters.
@Setter
// Constructor vacío para JPA.
@NoArgsConstructor
public class Inscripcion {
  // Clave primaria autoincremental.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Relación N:1 con Actividad (carga perezosa); es el lado propietario.
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  // FK actividad_id obligatoria.
  @JoinColumn(name = "actividad_id", nullable = false)
  private Actividad actividad;

  // Relación N:1 con Estudiante (carga perezosa); es el lado propietario.
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  // FK estudiante_id obligatoria.
  @JoinColumn(name = "estudiante_id", nullable = false)
  private Estudiante estudiante;

  // Fecha y hora de la inscripción: obligatoria.
  @Column(name = "fecha_inscripcion", nullable = false)
  private LocalDateTime fechaInscripcion;
}
