package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Departamento del centro. Su nombre es único; agrupa a varios docentes. */
@Entity
@Table(name = "departamento")
@Getter
@Setter
@NoArgsConstructor
public class Departamento {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 120)
  private String nombre;

  /** Lado inverso de Docente.departamento. */
  @OneToMany(mappedBy = "departamento")
  private List<Docente> docentes = new ArrayList<>();
}
