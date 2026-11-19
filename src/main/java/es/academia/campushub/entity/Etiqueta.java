// Paquete de las entidades JPA (tablas de PostgreSQL).
package es.academia.campushub.entity;

// Importa todas las anotaciones de JPA.
import jakarta.persistence.*;
// Implementación de conjunto sin duplicados.
import java.util.HashSet;
// Interfaz de conjunto.
import java.util.Set;
// Lombok: genera los getters.
import lombok.Getter;
// Lombok: genera el constructor vacío que exige JPA.
import lombok.NoArgsConstructor;
// Lombok: genera los setters.
import lombok.Setter;

/**
 * Etiqueta para clasificar actividades. Su nombre es único.
 *
 * @author Marcel Abad
 */
// Entidad persistente.
@Entity
// Tabla etiquetas.
@Table(name = "etiquetas")
// Genera getters.
@Getter
// Genera setters.
@Setter
// Constructor vacío para JPA.
@NoArgsConstructor
public class Etiqueta {
  // Clave primaria autoincremental.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Nombre: obligatorio, único en la BD, hasta 80 caracteres.
  @Column(nullable = false, unique = true, length = 80)
  private String nombre;

  // Lado inverso de la relación N:M; el propietario es Actividad.etiquetas.
  @ManyToMany(mappedBy = "etiquetas")
  private Set<Actividad> actividades = new HashSet<>();
}
