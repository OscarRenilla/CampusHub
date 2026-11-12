package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;

/** Casos de uso de organización y estudiantes (Persona A). Mismas firmas que CampusHubService. */
public interface OrganizacionService {
  DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request);

  DocenteResponse crearDocente(CreateDocenteRequest request);

  GrupoResponse crearGrupo(CreateGrupoRequest request);

  EstudianteResponse crearEstudiante(CreateEstudianteRequest request);

  EstudianteResponse obtenerEstudiante(Long id);

  ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request);

  ContactoResponse contacto(Long estudianteId);
}
