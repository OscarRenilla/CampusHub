package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.entity.*;
import org.springframework.stereotype.Component;

/** Conversión entidad -> DTO del bloque de organización. Usar dentro de una transacción. */
@Component
public class OrganizacionMapper {

  public DepartamentoResponse toResponse(Departamento d) {
    return new DepartamentoResponse(d.getId(), d.getNombre());
  }

  public DocenteResponse toResponse(Docente d) {
    return new DocenteResponse(
        d.getId(), d.getNombre(), d.getApellidos(), d.getEmail(), d.getDepartamento().getId());
  }

  public GrupoResponse toResponse(Grupo g) {
    return new GrupoResponse(g.getId(), g.getNombre(), g.getNivel(), g.getTutor().getId());
  }

  public EstudianteResponse toResponse(Estudiante e) {
    return new EstudianteResponse(
        e.getId(),
        e.getNombre(),
        e.getApellidos(),
        e.getEmail(),
        e.getFechaNacimiento().toString(),
        e.getGrupo().getId());
  }

  public ContactoResponse toResponse(ContactoEstudiante c) {
    return new ContactoResponse(
        c.getEstudiante().getId(),
        c.getTutorLegal(),
        c.getTelefono(),
        c.getEmail(),
        c.getAutorizado(),
        c.getFechaAutorizacion() == null ? null : c.getFechaAutorizacion().toString());
  }
}
