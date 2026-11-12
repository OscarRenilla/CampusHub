package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.entity.*;
import es.academia.campushub.mapper.ActividadMapper;
import es.academia.campushub.repository.*;
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActividadServiceImpl implements ActividadService {
  private final ActividadRepository actividades;
  private final EtiquetaRepository etiquetas;
  private final ActividadMapper mapper;

  public ActividadServiceImpl(
      ActividadRepository actividades, EtiquetaRepository etiquetas, ActividadMapper mapper) {
    this.actividades = actividades;
    this.etiquetas = etiquetas;
    this.mapper = mapper;
  }

  // ---------- Etiquetas (RF10) ----------

  @Override
  public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request) {
    requireBody(request);
    String nombre = texto(request.nombre(), "nombre", 80);
    if (etiquetas.existsByNombreIgnoreCase(nombre)) {
      throw new BusinessConflictException("Ya existe una etiqueta con ese nombre");
    }
    Etiqueta e = new Etiqueta();
    e.setNombre(nombre);
    // saveAndFlush envía el INSERT ya: si la BD detecta un duplicado, falla aquí y no más tarde.
    return mapper.toResponse(etiquetas.saveAndFlush(e));
  }

  @Override
  public void asociarEtiqueta(Long actividadId, Long etiquetaId) {
    Actividad actividad = actividad(actividadId);
    Etiqueta etiqueta =
        etiquetas
            .findById(etiquetaId)
            .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada"));
    // etiquetas es un Set: añadir una etiqueta que ya está no la duplica (la operación es
    // idempotente). Además la tabla actividad_etiqueta tiene clave única (actividad, etiqueta).
    actividad.getEtiquetas().add(etiqueta);
    actividades.saveAndFlush(actividad);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
    if (!etiquetas.existsById(etiquetaId)) {
      throw new ResourceNotFoundException("Etiqueta no encontrada");
    }
    return actividades.findByEtiquetasIdOrderById(etiquetaId).stream()
        .map(mapper::toResponse)
        .toList();
  }

  // ---------- Utilidades ----------

  private Actividad actividad(Long id) {
    return actividades
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
  }

  private static void requireBody(Object request) {
    if (request == null) {
      throw new InvalidInputException("Cuerpo de la petición obligatorio");
    }
  }

  private static String texto(String valor, String campo, int max) {
    if (valor == null || valor.isBlank()) {
      throw new InvalidInputException(campo + " es obligatorio");
    }
    String limpio = valor.trim();
    if (limpio.length() > max) {
      throw new InvalidInputException(campo + " supera " + max + " caracteres");
    }
    return limpio;
  }
}
