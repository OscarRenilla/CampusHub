package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Estudiante. Su email es único y siempre pertenece a un grupo existente. */
@Entity
@Table(name = "estudiante")
@Getter
@Setter
@NoArgsConstructor
public class Estudiante {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 150)
  private String apellidos;

  @Column(nullable = false, unique = true, length = 200)
  private String email;

  @Column(name = "fecha_nacimiento", nullable = false)
  private LocalDate fechaNacimiento;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "grupo_id", nullable = false)
  private Grupo grupo;

  /** Lado inverso de ContactoEstudiante.estudiante (el propietario es ContactoEstudiante). */
  @OneToOne(mappedBy = "estudiante", fetch = FetchType.LAZY)
  private ContactoEstudiante contacto;

  // INTEGRACION CON PERSONA B (Marcel): descomentar cuando exista la entidad Inscripcion
  // (su campo "estudiante" es el lado propietario). Es el lado inverso acordado en
  // docs/Acuerdo_entidades.md. Hay que importar java.util.ArrayList y java.util.List.
  //
  // @OneToMany(mappedBy = "estudiante")
  // private List<Inscripcion> inscripciones = new ArrayList<>();
}
