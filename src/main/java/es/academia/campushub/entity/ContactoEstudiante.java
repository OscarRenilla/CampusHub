package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ficha de contacto: como máximo una por estudiante (FK única) y no puede existir sin él (FK NOT
 * NULL). Es el lado propietario de la relación 1:1.
 */
@Entity
@Table(name = "contacto_estudiante")
@Getter
@Setter
@NoArgsConstructor
public class ContactoEstudiante {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estudiante_id", nullable = false, unique = true)
  private Estudiante estudiante;

  @Column(name = "tutor_legal", nullable = false, length = 200)
  private String tutorLegal;

  @Column(nullable = false, length = 30)
  private String telefono;

  @Column(nullable = false, length = 200)
  private String email;

  @Column(nullable = false)
  private Boolean autorizado;

  @Column(name = "fecha_autorizacion")
  private LocalDate fechaAutorizacion;
}
