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
 * Departamento del centro. Su nombre es único; agrupa a varios docentes.
 *
 * @author Álvaro Cotumba
 */
// Entidad persistente.
@Entity
// Tabla departamento.
@Table(name = "departamento")
// Genera getters.
@Getter
// Genera setters.
@Setter
// Constructor vacío para JPA.
@NoArgsConstructor
public class Departamento {
  // Clave primaria autoincremental.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Nombre: obligatorio, único en la BD y de hasta 120 caracteres.
  @Column(nullable = false, unique = true, length = 120)
  private String nombre;

  /** Lado inverso de Docente.departamento. */
  // mappedBy: la FK vive en la tabla docente.
  @OneToMany(mappedBy = "departamento")
  private List<Docente> docentes = new ArrayList<>();
}
