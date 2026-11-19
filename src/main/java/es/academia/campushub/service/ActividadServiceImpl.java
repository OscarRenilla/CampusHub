// Paquete de la capa de servicios (lógica de negocio).
package es.academia.campushub.service;

// Importa todos los DTO de petición y respuesta.
import es.academia.campushub.controller.dto.*;
// Importa todas las entidades JPA.
import es.academia.campushub.entity.*;
// Mapper de entidad a DTO del bloque de actividades.
import es.academia.campushub.mapper.ActividadMapper;
// Importa todos los repositorios.
import es.academia.campushub.repository.*;
// Excepciones propias que se traducen a códigos HTTP.
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
// Fecha sin hora (fecha prevista).
import java.time.LocalDate;
// Fecha con hora (momento de la inscripción).
import java.time.LocalDateTime;
// Excepción si una fecha no tiene el formato esperado.
import java.time.format.DateTimeParseException;
// Interfaz de lista.
import java.util.List;
// Marca la clase como servicio de Spring.
import org.springframework.stereotype.Service;
// Gestión de transacciones.
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de los casos de uso de actividades, inscripciones, historial y etiquetas.
 *
 * @author Marcel Abad
 */
// Servicio de Spring.
@Service
// Cada método se ejecuta en una transacción de escritura salvo que indique lo contrario.
@Transactional
public class ActividadServiceImpl implements ActividadService {
  // Acceso a la tabla de actividades.
  private final ActividadRepository actividades;
  // Acceso a la tabla de etiquetas.
  private final EtiquetaRepository etiquetas;
  // Acceso a la tabla de docentes (para validar el responsable).
  private final DocenteRepository docentes;
  // Acceso a la tabla de estudiantes (para inscribirlos).
  private final EstudianteRepository estudiantes;
  // Acceso a la tabla de inscripciones.
  private final InscripcionRepository inscripciones;
  // Conversor de entidades a DTO.
  private final ActividadMapper mapper;

  // Constructor: Spring inyecta todas las dependencias.
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

  // Crea una actividad: valida todos los campos y que el docente exista.
  @Override
  public ActividadResponse crearActividad(CreateActividadRequest request) {
    requireBody(request);
    // Valida los textos obligatorios con su longitud máxima.
    String titulo = texto(request.titulo(), "titulo", 200);
    String descripcion = texto(request.descripcion(), "descripcion", Integer.MAX_VALUE);
    String tipo = texto(request.tipo(), "tipo", 50);
    // Convierte la fecha prevista de texto a LocalDate.
    LocalDate fechaPrevista = fecha(request.fechaPrevista(), "fechaPrevista");
    // El aforo debe existir y ser mayor que cero.
    Integer max = request.maxParticipantes();
    if (max == null || max <= 0) {
      throw new InvalidInputException("maxParticipantes debe ser mayor que cero");
    }
    // El docente responsable es obligatorio.
    if (request.docenteId() == null) {
      throw new InvalidInputException("docenteId es obligatorio");
    }
    // El DTO trae docenteId; la entidad guarda el Docente completo. Si no existe, 404.
    Docente docente =
        docentes
            .findById(request.docenteId())
            .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado"));
    // Construye la entidad con los datos validados.
    Actividad a = new Actividad();
    a.setTitulo(titulo);
    a.setDescripcion(descripcion);
    a.setTipo(tipo);
    a.setFechaPrevista(fechaPrevista);
    a.setMaxParticipantes(max);
    a.setDocente(docente);
    // Guarda y devuelve el DTO.
    return mapper.toResponse(actividades.saveAndFlush(a));
  }

  // Obtiene una actividad por id (solo lectura).
  @Override
  @Transactional(readOnly = true)
  public ActividadResponse obtenerActividad(Long id) {
    return mapper.toResponse(actividad(id));
  }

  // ---------- Inscripciones (RF04) ----------

  // Inscribe a un estudiante: bloquea la actividad, evita duplicados y controla el aforo.
  @Override
  public InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request) {
    requireBody(request);
    // El estudiante es obligatorio.
    if (request.estudianteId() == null) {
      throw new InvalidInputException("estudianteId es obligatorio");
    }
    // 1) Bloqueamos la fila de la actividad hasta que acabe la transacción. Si llegan dos
    //    inscripciones a la vez, la segunda espera y ya ve la plaza ocupada por la primera.
    Actividad actividad =
        actividades
            .findByIdParaActualizar(actividadId)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    // Busca el estudiante; si no existe, 404.
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
    // Crea la inscripción con la actividad, el estudiante y la hora actual.
    Inscripcion inscripcion = new Inscripcion();
    inscripcion.setActividad(actividad);
    inscripcion.setEstudiante(estudiante);
    inscripcion.setFechaInscripcion(LocalDateTime.now());
    // Guarda y devuelve el DTO.
    return mapper.toResponse(inscripciones.saveAndFlush(inscripcion));
  }

  // Lista los estudiantes inscritos en una actividad (solo lectura).
  @Override
  @Transactional(readOnly = true)
  public List<EstudianteResponse> participantes(Long actividadId) {
    // Si la actividad no existe, 404.
    if (!actividades.existsById(actividadId)) {
      throw new ResourceNotFoundException("Actividad no encontrada");
    }
    // Carga las inscripciones con su estudiante y los convierte a DTO.
    return inscripciones.findByActividadIdConEstudiante(actividadId).stream()
        .map(mapper::toEstudiante)
        .toList();
  }

  // ---------- Historial del estudiante (RF08) ----------

  // Devuelve las actividades en las que está inscrito un estudiante (solo lectura).
  @Override
  @Transactional(readOnly = true)
  public List<ActividadHistorialResponse> historial(Long estudianteId) {
    // Si el estudiante no existe, 404.
    if (!estudiantes.existsById(estudianteId)) {
      throw new ResourceNotFoundException("Estudiante no encontrado");
    }
    // Carga las inscripciones con actividad y docente, y las convierte a DTO.
    return inscripciones.findHistorialByEstudianteId(estudianteId).stream()
        .map(mapper::toHistorial)
        .toList();
  }

  // ---------- Etiquetas (RF10) ----------

  // Crea una etiqueta con nombre único.
  @Override
  public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request) {
    requireBody(request);
    // Valida el nombre (obligatorio, máx. 80).
    String nombre = texto(request.nombre(), "nombre", 80);
    // Regla de negocio: no puede haber dos etiquetas con el mismo nombre.
    if (etiquetas.existsByNombreIgnoreCase(nombre)) {
      throw new BusinessConflictException("Ya existe una etiqueta con ese nombre");
    }
    // Crea la entidad.
    Etiqueta e = new Etiqueta();
    e.setNombre(nombre);
    // saveAndFlush envía el INSERT ya: si la BD detecta un duplicado, falla aquí y no más tarde.
    return mapper.toResponse(etiquetas.saveAndFlush(e));
  }

  // Asocia una etiqueta a una actividad (operación idempotente).
  @Override
  public void asociarEtiqueta(Long actividadId, Long etiquetaId) {
    // Busca la actividad (404 si no existe).
    Actividad actividad = actividad(actividadId);
    // Busca la etiqueta (404 si no existe).
    Etiqueta etiqueta =
        etiquetas
            .findById(etiquetaId)
            .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada"));
    // etiquetas es un Set: añadir una etiqueta que ya está no la duplica (la operación es
    // idempotente). Además la tabla actividad_etiqueta tiene clave única (actividad, etiqueta).
    actividad.getEtiquetas().add(etiqueta);
    // Guarda la actividad para persistir la relación.
    actividades.saveAndFlush(actividad);
  }

  // Lista las actividades que tienen una etiqueta (solo lectura).
  @Override
  @Transactional(readOnly = true)
  public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
    // Si la etiqueta no existe, 404.
    if (!etiquetas.existsById(etiquetaId)) {
      throw new ResourceNotFoundException("Etiqueta no encontrada");
    }
    // Busca las actividades de la etiqueta y las convierte a DTO.
    return actividades.findByEtiquetasIdOrderById(etiquetaId).stream()
        .map(mapper::toResponse)
        .toList();
  }

  // ---------- Utilidades ----------

  // Busca una actividad por id o lanza ResourceNotFoundException.
  private Actividad actividad(Long id) {
    return actividades
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
  }

  // Lanza error 400 si la petición no trae cuerpo.
  private static void requireBody(Object request) {
    if (request == null) {
      throw new InvalidInputException("Cuerpo de la petición obligatorio");
    }
  }

  // Valida un texto obligatorio: no nulo, no vacío y con longitud máxima. Devuelve el texto sin espacios.
  private static String texto(String valor, String campo, int max) {
    if (valor == null || valor.isBlank()) {
      throw new InvalidInputException(campo + " es obligatorio");
    }
    // Elimina espacios al principio y al final.
    String limpio = valor.trim();
    // Comprueba que no supere la longitud permitida.
    if (limpio.length() > max) {
      throw new InvalidInputException(campo + " supera " + max + " caracteres");
    }
    return limpio;
  }

  // Convierte un texto YYYY-MM-DD en LocalDate; si es inválido lanza error 400.
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
