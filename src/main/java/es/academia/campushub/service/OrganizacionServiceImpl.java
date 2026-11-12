package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.entity.*;
import es.academia.campushub.mapper.OrganizacionMapper;
import es.academia.campushub.repository.*;
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrganizacionServiceImpl implements OrganizacionService {
  private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  private final DepartamentoRepository departamentos;
  private final DocenteRepository docentes;
  private final GrupoRepository grupos;
  private final EstudianteRepository estudiantes;
  private final ContactoEstudianteRepository contactos;
  private final OrganizacionMapper mapper;

  public OrganizacionServiceImpl(
      DepartamentoRepository departamentos,
      DocenteRepository docentes,
      GrupoRepository grupos,
      EstudianteRepository estudiantes,
      ContactoEstudianteRepository contactos,
      OrganizacionMapper mapper) {
    this.departamentos = departamentos;
    this.docentes = docentes;
    this.grupos = grupos;
    this.estudiantes = estudiantes;
    this.contactos = contactos;
    this.mapper = mapper;
  }

  @Override
  public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request) {
    requireBody(request);
    String nombre = texto(request.nombre(), "nombre", 120);
    if (departamentos.existsByNombreIgnoreCase(nombre)) {
      throw new BusinessConflictException("Ya existe un departamento con ese nombre");
    }
    Departamento d = new Departamento();
    d.setNombre(nombre);
    return mapper.toResponse(departamentos.saveAndFlush(d));
  }

  @Override
  public DocenteResponse crearDocente(CreateDocenteRequest request) {
    requireBody(request);
    String nombre = texto(request.nombre(), "nombre", 100);
    String apellidos = texto(request.apellidos(), "apellidos", 150);
    String email = email(request.email(), "email");
    Long departamentoId = requerido(request.departamentoId(), "departamentoId");
    Departamento departamento =
        departamentos
            .findById(departamentoId)
            .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));
    if (docentes.existsByEmailIgnoreCase(email)) {
      throw new BusinessConflictException("Ya existe un docente con ese email");
    }
    Docente d = new Docente();
    d.setNombre(nombre);
    d.setApellidos(apellidos);
    d.setEmail(email);
    d.setDepartamento(departamento);
    return mapper.toResponse(docentes.saveAndFlush(d));
  }

  @Override
  public GrupoResponse crearGrupo(CreateGrupoRequest request) {
    requireBody(request);
    String nombre = texto(request.nombre(), "nombre", 100);
    String nivel = texto(request.nivel(), "nivel", 100);
    Long tutorId = requerido(request.tutorDocenteId(), "tutorDocenteId");
    // El DTO trae tutorDocenteId; la entidad guarda el Docente completo en la propiedad tutor.
    Docente tutor =
        docentes
            .findById(tutorId)
            .orElseThrow(() -> new ResourceNotFoundException("Docente tutor no encontrado"));
    Grupo g = new Grupo();
    g.setNombre(nombre);
    g.setNivel(nivel);
    g.setTutor(tutor);
    return mapper.toResponse(grupos.saveAndFlush(g));
  }

  @Override
  public EstudianteResponse crearEstudiante(CreateEstudianteRequest request) {
    requireBody(request);
    String nombre = texto(request.nombre(), "nombre", 100);
    String apellidos = texto(request.apellidos(), "apellidos", 150);
    String email = email(request.email(), "email");
    LocalDate nacimiento = fecha(request.fechaNacimiento(), "fechaNacimiento");
    if (nacimiento.isAfter(LocalDate.now())) {
      throw new InvalidInputException("fechaNacimiento no puede estar en el futuro");
    }
    Long grupoId = requerido(request.grupoId(), "grupoId");
    Grupo grupo =
        grupos
            .findById(grupoId)
            .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
    if (estudiantes.existsByEmailIgnoreCase(email)) {
      throw new BusinessConflictException("Ya existe un estudiante con ese email");
    }
    Estudiante e = new Estudiante();
    e.setNombre(nombre);
    e.setApellidos(apellidos);
    e.setEmail(email);
    e.setFechaNacimiento(nacimiento);
    e.setGrupo(grupo);
    return mapper.toResponse(estudiantes.saveAndFlush(e));
  }

  @Override
  @Transactional(readOnly = true)
  public EstudianteResponse obtenerEstudiante(Long id) {
    return mapper.toResponse(estudiante(id));
  }

  @Override
  public ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request) {
    requireBody(request);
    Estudiante estudiante = estudiante(estudianteId);
    String tutorLegal = texto(request.tutorLegal(), "tutorLegal", 200);
    String telefono = texto(request.telefono(), "telefono", 30);
    String email = email(request.email(), "email");
    Boolean autorizado = requerido(request.autorizado(), "autorizado");
    LocalDate fechaAutorizacion =
        request.fechaAutorizacion() == null
            ? null
            : fecha(request.fechaAutorizacion(), "fechaAutorizacion");

    // Upsert: si ya hay ficha se actualiza; la FK única impide además duplicarla en la BD.
    ContactoEstudiante c =
        contactos
            .findByEstudianteId(estudianteId)
            .orElseGet(
                () -> {
                  ContactoEstudiante nuevo = new ContactoEstudiante();
                  nuevo.setEstudiante(estudiante);
                  return nuevo;
                });
    c.setTutorLegal(tutorLegal);
    c.setTelefono(telefono);
    c.setEmail(email);
    c.setAutorizado(autorizado);
    c.setFechaAutorizacion(fechaAutorizacion);
    return mapper.toResponse(contactos.saveAndFlush(c));
  }

  @Override
  @Transactional(readOnly = true)
  public ContactoResponse contacto(Long estudianteId) {
    estudiante(estudianteId);
    return contactos
        .findByEstudianteId(estudianteId)
        .map(mapper::toResponse)
        .orElseThrow(
            () -> new ResourceNotFoundException("El estudiante no tiene ficha de contacto"));
  }

  // ---------- Utilidades ----------

  private Estudiante estudiante(Long id) {
    return estudiantes
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
  }

  private static void requireBody(Object request) {
    if (request == null) {
      throw new InvalidInputException("Cuerpo de la petición obligatorio");
    }
  }

  private static <T> T requerido(T valor, String campo) {
    if (valor == null) {
      throw new InvalidInputException(campo + " es obligatorio");
    }
    return valor;
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

  private static String email(String valor, String campo) {
    String limpio = texto(valor, campo, 200);
    if (!EMAIL.matcher(limpio).matches()) {
      throw new InvalidInputException(campo + " no tiene un formato válido");
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
