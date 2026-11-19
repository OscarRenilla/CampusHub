// Paquete de la capa de servicios (lógica de negocio).
package es.academia.campushub.service;

// Importa todos los DTO de petición y respuesta.
import es.academia.campushub.controller.dto.*;
// Interfaz de lista.
import java.util.List;

/**
 * Casos de uso de actividades, etiquetas e inscripciones. Mismas firmas que CampusHubService.
 *
 * @author Marcel Abad
 */
public interface ActividadService {
  // Crea una actividad nueva validando los datos y el docente.
  ActividadResponse crearActividad(CreateActividadRequest request);

  // Obtiene una actividad por su id.
  ActividadResponse obtenerActividad(Long id);

  // Inscribe a un estudiante en una actividad respetando duplicados y aforo.
  InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request);

  // Lista los estudiantes inscritos en una actividad.
  List<EstudianteResponse> participantes(Long actividadId);

  // Devuelve el historial de actividades de un estudiante.
  List<ActividadHistorialResponse> historial(Long estudianteId);

  // Crea una etiqueta nueva con nombre único.
  EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request);

  // Asocia una etiqueta existente a una actividad existente.
  void asociarEtiqueta(Long actividadId, Long etiquetaId);

  // Lista las actividades que tienen una etiqueta concreta.
  List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId);
}
