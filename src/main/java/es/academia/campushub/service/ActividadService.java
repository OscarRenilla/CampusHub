package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import java.util.List;

/**
 * Casos de uso de actividades, etiquetas e inscripciones (Persona B). Mismas firmas que
 * CampusHubService.
 */
public interface ActividadService {
  ActividadResponse crearActividad(CreateActividadRequest request);

  ActividadResponse obtenerActividad(Long id);

  InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request);

  EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request);

  void asociarEtiqueta(Long actividadId, Long etiquetaId);

  List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId);
}
