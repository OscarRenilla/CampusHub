// Paquete de la capa de servicios (lógica de negocio).
package es.academia.campushub.service;

// Importa todos los DTO de petición y respuesta.
import es.academia.campushub.controller.dto.*;
// Interfaz de lista.
import java.util.List;

/**
 * Fachada con todos los casos de uso de CampusHub. Los controladores solo conocen esta interfaz;
 * la implementación reparte el trabajo entre los servicios de cada persona.
 */
public interface CampusHubService {
  // Crea un grupo. @author Álvaro Cotumba
  GrupoResponse crearGrupo(CreateGrupoRequest request);

  // Crea un departamento. @author Álvaro Cotumba
  DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request);

  // Crea un estudiante. @author Álvaro Cotumba
  EstudianteResponse crearEstudiante(CreateEstudianteRequest request);

  // Obtiene un estudiante por id. @author Álvaro Cotumba
  EstudianteResponse obtenerEstudiante(Long id);

  // Crea un docente. @author Álvaro Cotumba
  DocenteResponse crearDocente(CreateDocenteRequest request);

  // Crea una actividad. @author Marcel Abad
  ActividadResponse crearActividad(CreateActividadRequest request);

  // Obtiene una actividad por id. @author Marcel Abad
  ActividadResponse obtenerActividad(Long id);

  // Inscribe a un estudiante en una actividad. @author Marcel Abad
  InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request);

  // Lista los participantes de una actividad. @author Marcel Abad
  List<EstudianteResponse> participantes(Long actividadId);

  // Registra un seguimiento en MongoDB. @author Óscar Renilla
  SeguimientoResponse registrarSeguimiento(Long actividadId, CreateSeguimientoRequest request);

  // Devuelve el detalle completo de una actividad. @author Óscar Renilla
  ActividadDetalleResponse detalle(Long id);

  // Devuelve el historial de actividades de un estudiante. @author Marcel Abad
  List<ActividadHistorialResponse> historial(Long estudianteId);

  // Crea o actualiza la ficha de contacto de un estudiante. @author Álvaro Cotumba
  ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request);

  // Obtiene la ficha de contacto de un estudiante. @author Álvaro Cotumba
  ContactoResponse contacto(Long estudianteId);

  // Crea una etiqueta. @author Marcel Abad
  EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request);

  // Asocia una etiqueta a una actividad. @author Marcel Abad
  void asociarEtiqueta(Long actividadId, Long etiquetaId);

  // Lista las actividades de una etiqueta. @author Marcel Abad
  List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId);
}
