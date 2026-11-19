// Paquete de las entidades JPA (tablas de PostgreSQL).
package es.academia.campushub.entity;

// Importa todas las anotaciones de JPA.
import jakarta.persistence.*;
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
 * Grupo de estudiantes con un docente tutor.
 *
 * @author Álvaro Cotumba
 */
// Entidad persistente.
@Entity
// Tabla grupo.
@Table(name = "grupo")
// Genera getters.
@Getter
// Genera setters.
@Setter
// Constructor vacío para JPA.
@NoArgsConstructor
public class Grupo {
  // Clave primaria autoincremental.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Nombre del grupo: obligatorio, hasta 100 caracteres.
  @Column(nullable = false, length = 100)
  private String nombre;

  // Nivel educativo: obligatorio, hasta 100 caracteres.
  @Column(nullable = false, length = 100)
  private String nivel;

  /** Propiedad tutor (entidad). El DTO usa tutorDocenteId, que el servicio resuelve. */
  // Relación N:1 con Docente (carga perezosa); todo grupo tiene tutor.
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  // FK tutor_docente_id obligatoria.
  @JoinColumn(name = "tutor_docente_id", nullable = false)
  private Docente tutor;

  /** Lado inverso de Estudiante.grupo. */
  // Relación 1:N inversa: un grupo tiene muchos estudiantes.
  @OneToMany(mappedBy = "grupo")
  private List<Estudiante> estudiantes = new ArrayList<>();
}
