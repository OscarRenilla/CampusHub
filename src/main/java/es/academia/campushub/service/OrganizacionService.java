// Paquete de la capa de servicios (lógica de negocio).
package es.academia.campushub.service;

// Importa todos los DTO de petición y respuesta.
import es.academia.campushub.controller.dto.*;

/**
 * Casos de uso de organización y estudiantes. Mismas firmas que CampusHubService.
 *
 * @author Álvaro Cotumba
 */
public interface OrganizacionService {
  // Crea un departamento con nombre único.
  DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request);

  // Crea un docente asociado a un departamento existente.
  DocenteResponse crearDocente(CreateDocenteRequest request);

  // Crea un grupo con un docente tutor existente.
  GrupoResponse crearGrupo(CreateGrupoRequest request);

  // Crea un estudiante dentro de un grupo existente.
  EstudianteResponse crearEstudiante(CreateEstudianteRequest request);

  // Obtiene un estudiante por id.
  EstudianteResponse obtenerEstudiante(Long id);

  // Crea o actualiza (upsert) la ficha de contacto de un estudiante.
  ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request);

  // Obtiene la ficha de contacto de un estudiante.
  ContactoResponse contacto(Long estudianteId);
}
