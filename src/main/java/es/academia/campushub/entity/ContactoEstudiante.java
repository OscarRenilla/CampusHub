// Paquete de las entidades JPA (tablas de PostgreSQL).
package es.academia.campushub.entity;

// Importa todas las anotaciones de JPA.
import jakarta.persistence.*;
// Tipo de fecha sin hora para la fecha de autorización.
import java.time.LocalDate;
// Lombok: genera los getters.
import lombok.Getter;
// Lombok: genera el constructor vacío que exige JPA.
import lombok.NoArgsConstructor;
// Lombok: genera los setters.
import lombok.Setter;

/**
 * Ficha de contacto: como máximo una por estudiante (FK única) y no puede existir sin él (FK NOT
 * NULL). Es el lado propietario de la relación 1:1.
 *
 * @author Álvaro Cotumba
 */
// Entidad persistente.
@Entity
// Tabla contacto_estudiante.
@Table(name = "contacto_estudiante")
// Genera getters.
@Getter
// Genera setters.
@Setter
// Constructor vacío para JPA.
@NoArgsConstructor
public class ContactoEstudiante {
  // Clave primaria autoincremental.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Relación 1:1 con Estudiante, cargada de forma perezosa; la ficha no existe sin estudiante.
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  // FK estudiante_id obligatoria y única: garantiza máximo una ficha por estudiante.
  @JoinColumn(name = "estudiante_id", nullable = false, unique = true)
  private Estudiante estudiante;

  // Nombre del tutor legal: obligatorio, hasta 200 caracteres.
  @Column(name = "tutor_legal", nullable = false, length = 200)
  private String tutorLegal;

  // Teléfono: obligatorio, hasta 30 caracteres.
  @Column(nullable = false, length = 30)
  private String telefono;

  // Email del tutor: obligatorio, hasta 200 caracteres.
  @Column(nullable = false, length = 200)
  private String email;

  // Si el tutor autoriza la participación: obligatorio.
  @Column(nullable = false)
  private Boolean autorizado;

  // Fecha de autorización: opcional (puede ser null).
  @Column(name = "fecha_autorizacion")
  private LocalDate fechaAutorizacion;
}
