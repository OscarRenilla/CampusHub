package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

/**
 * Actividad organizada por un docente. La regla "máximo de participantes > 0" se protege en la
 * propia base de datos con un CHECK, no solo en la API.
 */
@Entity
@Table(
    name = "actividades",
    indexes = @Index(name = "idx_actividad_docente", columnList = "docente_id"))
@Check(name = "ck_actividad_max_participantes", constraints = "max_participantes > 0")
@Getter
@Setter
@NoArgsConstructor
public class Actividad {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String titulo;

  @Column(nullable = false, columnDefinition = "text")
  private String descripcion;

  @Column(nullable = false, length = 50)
  private String tipo;

  @Column(name = "fecha_prevista", nullable = false)
  private LocalDate fechaPrevista;

  @Column(name = "max_participantes", nullable = false)
  private Integer maxParticipantes;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "docente_id", nullable = false)
  private Docente docente;

  @ManyToMany
  @JoinTable(
      name = "actividad_etiqueta",
      joinColumns = @JoinColumn(name = "actividad_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "etiqueta_id", nullable = false),
      uniqueConstraints =
          @UniqueConstraint(
              name = "uk_actividad_etiqueta",
              columnNames = {"actividad_id", "etiqueta_id"}))
  private Set<Etiqueta> etiquetas = new HashSet<>();

  /** Lado inverso de Inscripcion.actividad (el propietario es Inscripcion). */
  @OneToMany(mappedBy = "actividad")
  private List<Inscripcion> inscripciones = new ArrayList<>();
}
