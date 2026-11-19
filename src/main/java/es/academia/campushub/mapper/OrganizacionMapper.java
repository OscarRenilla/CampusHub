// Paquete de los conversores entre entidades y DTO.
package es.academia.campushub.mapper;

// DTO de respuesta a los que se convierten las entidades.
import es.academia.campushub.controller.dto.*;
// Entidades JPA de origen.
import es.academia.campushub.entity.*;
// Marca la clase como componente de Spring.
import org.springframework.stereotype.Component;

/**
 * Conversión entidad -> DTO del bloque de organización. Usar dentro de una transacción.
 *
 * @author Álvaro Cotumba
 */
// Componente gestionado por Spring (singleton).
@Component
public class OrganizacionMapper {

  // Convierte un Departamento en DTO.
  public DepartamentoResponse toResponse(Departamento d) {
    return new DepartamentoResponse(d.getId(), d.getNombre());
  }

  // Convierte un Docente en DTO; el departamento se expone solo por su id.
  public DocenteResponse toResponse(Docente d) {
    return new DocenteResponse(
        d.getId(), d.getNombre(), d.getApellidos(), d.getEmail(), d.getDepartamento().getId());
  }

  // Convierte un Grupo en DTO; el tutor se expone solo por su id.
  public GrupoResponse toResponse(Grupo g) {
    return new GrupoResponse(g.getId(), g.getNombre(), g.getNivel(), g.getTutor().getId());
  }

  // Convierte un Estudiante en DTO; el grupo se expone solo por su id.
  public EstudianteResponse toResponse(Estudiante e) {
    return new EstudianteResponse(
        // Id del estudiante.
        e.getId(),
        // Nombre.
        e.getNombre(),
        // Apellidos.
        e.getApellidos(),
        // Email.
        e.getEmail(),
        // Fecha de nacimiento como texto (YYYY-MM-DD).
        e.getFechaNacimiento().toString(),
        // Id del grupo.
        e.getGrupo().getId());
  }

  // Convierte una ficha de contacto en DTO.
  public ContactoResponse toResponse(ContactoEstudiante c) {
    return new ContactoResponse(
        // Id del estudiante propietario de la ficha.
        c.getEstudiante().getId(),
        // Tutor legal.
        c.getTutorLegal(),
        // Teléfono.
        c.getTelefono(),
        // Email.
        c.getEmail(),
        // Si está autorizado.
        c.getAutorizado(),
        // La fecha es opcional: si es null se devuelve null, si no, se pasa a texto.
        c.getFechaAutorizacion() == null ? null : c.getFechaAutorizacion().toString());
  }
}
