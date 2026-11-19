// Paquete de las entidades JPA (tablas de PostgreSQL).
package es.academia.campushub.entity;

// Importa todas las anotaciones de JPA.
import jakarta.persistence.*;
// Lombok: genera los getters.
import lombok.Getter;
// Lombok: genera el constructor vacío que exige JPA.
import lombok.NoArgsConstructor;
// Lombok: genera los setters.
import lombok.Setter;

/**
 * Docente del centro. Pertenece a un departamento y su email es único.
 *
 * @author Álvaro Cotumba
 */
// Entidad persistente.
@Entity
// Tabla docente.
@Table(name = "docente")
// Genera getters.
@Getter
// Genera setters.
@Setter
// Constructor vacío para JPA.
@NoArgsConstructor
public class Docente {
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

  // Relación N:1 con Departamento (cargada de forma perezosa); todo docente tiene departamento.
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  // FK departamento_id obligatoria.
  @JoinColumn(name = "departamento_id", nullable = false)
  private Departamento departamento;
}
