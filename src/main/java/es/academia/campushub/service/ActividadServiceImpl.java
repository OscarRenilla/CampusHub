package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.entity.*;
import es.academia.campushub.mapper.ActividadMapper;
import es.academia.campushub.repository.*;
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActividadServiceImpl implements ActividadService {
  private final ActividadRepository actividades;
  private final EtiquetaRepository etiquetas;
  private final DocenteRepository docentes;
  private final ActividadMapper mapper;

  public ActividadServiceImpl(
      ActividadRepository actividades,
      EtiquetaRepository etiquetas,
      DocenteRepository docentes,
      ActividadMapper mapper) {
    this.actividades = actividades;
    this.etiquetas = etiquetas;
    this.docentes = docentes;
    this.mapper = mapper;
  }

  // ---------- Actividades (RF03) ----------

  @Override
  public ActividadResponse crearActividad(CreateActividadRequest request) {
    requireBody(request);
    String titulo = texto(request.titulo(), "titulo", 200);
    String descripcion = texto(request.descripcion(), "descripcion", Integer.MAX_VALUE);
    String tipo = texto(request.tipo(), "tipo", 50);
    LocalDate fechaPrevista = fecha(request.fechaPrevista(), "fechaPrevista");
    Integer max = request.maxParticipantes();
    if (max == null || max <= 0) {
      throw new InvalidInputException("maxParticipantes debe ser mayor que cero");
    }
    if (request.docenteId() == null) {
      throw new InvalidInputException("docenteId es obligatorio");
    }
    // El DTO trae docenteId; la entidad guarda el Docente completo. Si no existe, 404.
    Docente docente =
        docentes
            .findById(request.docenteId())
            .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado"));
    Actividad a = new Actividad();
    a.setTitulo(titulo);
    a.setDescripcion(descripcion);
    a.setTipo(tipo);
    a.setFechaPrevista(fechaPrevista);
    a.setMaxParticipantes(max);
    a.setDocente(docente);
    return mapper.toResponse(actividades.saveAndFlush(a));
  }

  @Override
  @Transactional(readOnly = true)
  public ActividadResponse obtenerActividad(Long id) {
    return mapper.toResponse(actividad(id));
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

  private static LocalDate fecha(String valor, String campo) {
    if (valor == null || valor.isBlank()) {
      throw new InvalidInputException(campo + " es obligatorio");
    }
    try {
      return LocalDate.parse(valor.trim());
    } catch (DateTimeParseException ex) {
      throw new InvalidInputException(campo + " debe tener formato YYYY-MM-DD");
    }
  }
}
