package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.entity.*;
import es.academia.campushub.mapper.ActividadMapper;
import es.academia.campushub.repository.*;
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  private final EstudianteRepository estudiantes;
  private final InscripcionRepository inscripciones;
  private final ActividadMapper mapper;

  public ActividadServiceImpl(
      ActividadRepository actividades,
      EtiquetaRepository etiquetas,
      DocenteRepository docentes,
      EstudianteRepository estudiantes,
      InscripcionRepository inscripciones,
      ActividadMapper mapper) {
    this.actividades = actividades;
    this.etiquetas = etiquetas;
    this.docentes = docentes;
    this.estudiantes = estudiantes;
    this.inscripciones = inscripciones;
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

  // ---------- Inscripciones (RF04) ----------

  @Override
  public InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request) {
    requireBody(request);
    if (request.estudianteId() == null) {
      throw new InvalidInputException("estudianteId es obligatorio");
    }
    // 1) Bloqueamos la fila de la actividad hasta que acabe la transacción. Si llegan dos
    //    inscripciones a la vez, la segunda espera y ya ve la plaza ocupada por la primera.
    Actividad actividad =
        actividades
            .findByIdParaActualizar(actividadId)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    Estudiante estudiante =
        estudiantes
            .findById(request.estudianteId())
            .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
    // 2) No se permite inscribirse dos veces (además, la BD tiene UNIQUE como red de seguridad).
    if (inscripciones.existsByActividadIdAndEstudianteId(actividadId, estudiante.getId())) {
      throw new BusinessConflictException("El estudiante ya está inscrito en la actividad");
    }
    // 3) Aforo: no se admite una inscripción si ya se ocuparon todas las plazas.
    if (inscripciones.countByActividadId(actividadId) >= actividad.getMaxParticipantes()) {
      throw new BusinessConflictException("La actividad no tiene plazas disponibles");
    }
    Inscripcion inscripcion = new Inscripcion();
    inscripcion.setActividad(actividad);
    inscripcion.setEstudiante(estudiante);
    inscripcion.setFechaInscripcion(LocalDateTime.now());
    return mapper.toResponse(inscripciones.saveAndFlush(inscripcion));
  }

  @Override
  @Transactional(readOnly = true)
  public List<EstudianteResponse> participantes(Long actividadId) {
    if (!actividades.existsById(actividadId)) {
      throw new ResourceNotFoundException("Actividad no encontrada");
    }
    return inscripciones.findByActividadIdConEstudiante(actividadId).stream()
        .map(mapper::toEstudiante)
        .toList();
  }

  // ---------- Historial del estudiante (RF08) ----------

  @Override
  @Transactional(readOnly = true)
  public List<ActividadHistorialResponse> historial(Long estudianteId) {
    if (!estudiantes.existsById(estudianteId)) {
      throw new ResourceNotFoundException("Estudiante no encontrado");
    }
    return inscripciones.findHistorialByEstudianteId(estudianteId).stream()
        .map(mapper::toHistorial)
        .toList();
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
