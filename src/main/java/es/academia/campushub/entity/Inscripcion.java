package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inscripción de un estudiante en una actividad.
 *
 * <p>La base de datos impide duplicados con UNIQUE(actividad_id, estudiante_id) y exige que ambas
 * claves foráneas existan. El índice por estudiante acelera la consulta del historial.
 */
@Entity
@Table(
    name = "inscripciones",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_inscripcion_actividad_estudiante",
            columnNames = {"actividad_id", "estudiante_id"}),
    indexes = @Index(name = "idx_inscripcion_estudiante", columnList = "estudiante_id"))
@Getter
@Setter
@NoArgsConstructor
public class Inscripcion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "actividad_id", nullable = false)
  private Actividad actividad;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estudiante_id", nullable = false)
  private Estudiante estudiante;

  @Column(name = "fecha_inscripcion", nullable = false)
  private LocalDateTime fechaInscripcion;
}
