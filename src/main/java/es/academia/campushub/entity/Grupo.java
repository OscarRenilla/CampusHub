package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Grupo de estudiantes con un docente tutor. */
@Entity
@Table(name = "grupo")
@Getter
@Setter
@NoArgsConstructor
public class Grupo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 100)
  private String nivel;

  /** Propiedad "tutor" (entidad). El DTO usa tutorDocenteId, que el servicio resuelve. */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tutor_docente_id", nullable = false)
  private Docente tutor;

  /** Lado inverso de Estudiante.grupo. */
  @OneToMany(mappedBy = "grupo")
  private List<Estudiante> estudiantes = new ArrayList<>();
}
