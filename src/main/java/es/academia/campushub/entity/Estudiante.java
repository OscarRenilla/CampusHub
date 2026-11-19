// Paquete de las entidades JPA (tablas de PostgreSQL).
package es.academia.campushub.entity;

// Importa todas las anotaciones de JPA.
import jakarta.persistence.*;
// Tipo de fecha sin hora para la fecha de nacimiento.
import java.time.LocalDate;
// Implementación de lista.
import java.util.ArrayList;
// Interfaz de lista.
import java.util.List;
// Lombok: genera los getters.
import lombok.Getter;
// Lombok: genera el constructor vacío que exige JPA.
import lombok.NoArgsConstructor;
// Lombok: genera los setters.
import lombok.Setter;

/**
 * Estudiante. Su email es único y siempre pertenece a un grupo existente.
 *
 * @author Álvaro Cotumba
 */
// Entidad persistente.
@Entity
// Tabla estudiante.
@Table(name = "estudiante")
// Genera getters.
@Getter
// Genera setters.
@Setter
// Constructor vacío para JPA.
@NoArgsConstructor
public class Estudiante {
  // Clave primaria autoincremental.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Nombre de pila: obligatorio, hasta 100 caracteres.
  @Column(nullable = false, length = 100)
  private String nombre;

  // Apellidos: obligatorios, hasta 150 caracteres.
  @Column(nullable = false, length = 150)
  private String apellidos;

  // Email: obligatorio, único en la BD, hasta 200 caracteres.
  @Column(nullable = false, unique = true, length = 200)
  private String email;

  // Fecha de nacimiento: obligatoria.
  @Column(name = "fecha_nacimiento", nullable = false)
  private LocalDate fechaNacimiento;

  // Relación N:1 con Grupo (carga perezosa); todo estudiante pertenece a un grupo.
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  // FK grupo_id obligatoria.
  @JoinColumn(name = "grupo_id", nullable = false)
  private Grupo grupo;

  /** Lado inverso de ContactoEstudiante.estudiante (el propietario es ContactoEstudiante). */
  // Relación 1:1 inversa: la FK está en contacto_estudiante.
  @OneToOne(mappedBy = "estudiante", fetch = FetchType.LAZY)
  private ContactoEstudiante contacto;

  /** Lado inverso de Inscripcion.estudiante (el propietario es Inscripcion). */
  // Relación 1:N inversa: un estudiante puede tener muchas inscripciones.
  @OneToMany(mappedBy = "estudiante")
  private List<Inscripcion> inscripciones = new ArrayList<>();
}
