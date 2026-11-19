// Paquete de la capa de servicios (lógica de negocio).
package es.academia.campushub.service;

// Importa todos los DTO de petición y respuesta.
import es.academia.campushub.controller.dto.*;
// Importa todas las entidades JPA.
import es.academia.campushub.entity.*;
// Mapper de entidad a DTO del bloque de organización.
import es.academia.campushub.mapper.OrganizacionMapper;
// Importa todos los repositorios.
import es.academia.campushub.repository.*;
// Excepciones propias que se traducen a códigos HTTP.
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
// Tipo de fecha sin hora.
import java.time.LocalDate;
// Excepción que salta si una fecha no tiene el formato esperado.
import java.time.format.DateTimeParseException;
// Expresiones regulares para validar el email.
import java.util.regex.Pattern;
// Marca la clase como servicio de Spring.
import org.springframework.stereotype.Service;
// Gestión de transacciones: todo el método se confirma o se deshace a la vez.
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de los casos de uso de organización y estudiantes: departamentos, docentes,
 * grupos, estudiantes y fichas de contacto.
 *
 * @author Álvaro Cotumba
 */
// Servicio de Spring.
@Service
// Cada método público se ejecuta dentro de una transacción de escritura salvo que indique lo contrario.
@Transactional
public class OrganizacionServiceImpl implements OrganizacionService {
  // Patrón básico de email: texto, una arroba, texto, un punto y texto, sin espacios.
  private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  // Acceso a la tabla de departamentos.
  private final DepartamentoRepository departamentos;
  // Acceso a la tabla de docentes.
  private final DocenteRepository docentes;
  // Acceso a la tabla de grupos.
  private final GrupoRepository grupos;
  // Acceso a la tabla de estudiantes.
  private final EstudianteRepository estudiantes;
  // Acceso a la tabla de fichas de contacto.
  private final ContactoEstudianteRepository contactos;
  // Conversor de entidades a DTO.
  private final OrganizacionMapper mapper;

  // Constructor: Spring inyecta todas las dependencias.
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

  // Crea un departamento: valida el nombre y comprueba que no esté repetido.
  @Override
  public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request) {
    // Rechaza peticiones sin cuerpo.
    requireBody(request);
    // Valida el nombre (obligatorio, máx. 120) y le quita espacios sobrantes.
    String nombre = texto(request.nombre(), "nombre", 120);
    // Regla de negocio: el nombre debe ser único sin distinguir mayúsculas.
    if (departamentos.existsByNombreIgnoreCase(nombre)) {
      throw new BusinessConflictException("Ya existe un departamento con ese nombre");
    }
    // Crea la entidad y le asigna el nombre.
    Departamento d = new Departamento();
    d.setNombre(nombre);
    // saveAndFlush guarda y envía el INSERT ya; después se convierte a DTO.
    return mapper.toResponse(departamentos.saveAndFlush(d));
  }

  // Crea un docente: valida datos, comprueba el departamento y que el email no exista.
  @Override
  public DocenteResponse crearDocente(CreateDocenteRequest request) {
    requireBody(request);
    // Valida nombre, apellidos y email.
    String nombre = texto(request.nombre(), "nombre", 100);
    String apellidos = texto(request.apellidos(), "apellidos", 150);
    String email = email(request.email(), "email");
    // El departamento es obligatorio.
    Long departamentoId = requerido(request.departamentoId(), "departamentoId");
    // Busca el departamento; si no existe, error 404.
    Departamento departamento =
        departamentos
            .findById(departamentoId)
            .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));
    // Regla de negocio: el email del docente debe ser único.
    if (docentes.existsByEmailIgnoreCase(email)) {
      throw new BusinessConflictException("Ya existe un docente con ese email");
    }
    // Construye la entidad con los datos validados.
    Docente d = new Docente();
    d.setNombre(nombre);
    d.setApellidos(apellidos);
    d.setEmail(email);
    d.setDepartamento(departamento);
    // Guarda y devuelve el DTO.
    return mapper.toResponse(docentes.saveAndFlush(d));
  }

  // Crea un grupo: valida datos y resuelve el docente tutor.
  @Override
  public GrupoResponse crearGrupo(CreateGrupoRequest request) {
    requireBody(request);
    // Valida nombre y nivel.
    String nombre = texto(request.nombre(), "nombre", 100);
    String nivel = texto(request.nivel(), "nivel", 100);
    // El tutor es obligatorio.
    Long tutorId = requerido(request.tutorDocenteId(), "tutorDocenteId");
    // El DTO trae tutorDocenteId; la entidad guarda el Docente completo en la propiedad tutor.
    Docente tutor =
        docentes
            .findById(tutorId)
            .orElseThrow(() -> new ResourceNotFoundException("Docente tutor no encontrado"));
    // Construye la entidad.
    Grupo g = new Grupo();
    g.setNombre(nombre);
    g.setNivel(nivel);
    g.setTutor(tutor);
    // Guarda y devuelve el DTO.
    return mapper.toResponse(grupos.saveAndFlush(g));
  }

  // Crea un estudiante: valida datos, fecha, grupo y unicidad del email.
  @Override
  public EstudianteResponse crearEstudiante(CreateEstudianteRequest request) {
    requireBody(request);
    // Valida nombre, apellidos y email.
    String nombre = texto(request.nombre(), "nombre", 100);
    String apellidos = texto(request.apellidos(), "apellidos", 150);
    String email = email(request.email(), "email");
    // Convierte la fecha de nacimiento de texto a LocalDate.
    LocalDate nacimiento = fecha(request.fechaNacimiento(), "fechaNacimiento");
    // Una fecha de nacimiento futura no es válida.
    if (nacimiento.isAfter(LocalDate.now())) {
      throw new InvalidInputException("fechaNacimiento no puede estar en el futuro");
    }
    // El grupo es obligatorio y debe existir.
    Long grupoId = requerido(request.grupoId(), "grupoId");
    Grupo grupo =
        grupos
            .findById(grupoId)
            .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
    // Regla de negocio: el email del estudiante debe ser único.
    if (estudiantes.existsByEmailIgnoreCase(email)) {
      throw new BusinessConflictException("Ya existe un estudiante con ese email");
    }
    // Construye la entidad.
    Estudiante e = new Estudiante();
    e.setNombre(nombre);
    e.setApellidos(apellidos);
    e.setEmail(email);
    e.setFechaNacimiento(nacimiento);
    e.setGrupo(grupo);
    // Guarda y devuelve el DTO.
    return mapper.toResponse(estudiantes.saveAndFlush(e));
  }

  // Obtiene un estudiante; transacción de solo lectura porque no modifica nada.
  @Override
  @Transactional(readOnly = true)
  public EstudianteResponse obtenerEstudiante(Long id) {
    return mapper.toResponse(estudiante(id));
  }

  // Crea o actualiza la ficha de contacto (upsert) de un estudiante.
  @Override
  public ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request) {
    requireBody(request);
    // El estudiante debe existir (404 si no).
    Estudiante estudiante = estudiante(estudianteId);
    // Valida los campos obligatorios de la ficha.
    String tutorLegal = texto(request.tutorLegal(), "tutorLegal", 200);
    String telefono = texto(request.telefono(), "telefono", 30);
    String email = email(request.email(), "email");
    Boolean autorizado = requerido(request.autorizado(), "autorizado");
    // La fecha de autorización es opcional: si viene, se valida su formato.
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
                  // No existía: se crea una ficha nueva asociada al estudiante.
                  ContactoEstudiante nuevo = new ContactoEstudiante();
                  nuevo.setEstudiante(estudiante);
                  return nuevo;
                });
    // Copia los datos validados sobre la ficha (nueva o existente).
    c.setTutorLegal(tutorLegal);
    c.setTelefono(telefono);
    c.setEmail(email);
    c.setAutorizado(autorizado);
    c.setFechaAutorizacion(fechaAutorizacion);
    // Guarda y devuelve el DTO.
    return mapper.toResponse(contactos.saveAndFlush(c));
  }

  // Obtiene la ficha de contacto de un estudiante (solo lectura).
  @Override
  @Transactional(readOnly = true)
  public ContactoResponse contacto(Long estudianteId) {
    // Comprueba que el estudiante existe; si no, lanza 404.
    estudiante(estudianteId);
    // Busca la ficha; si el estudiante no tiene, lanza 404.
    return contactos
        .findByEstudianteId(estudianteId)
        .map(mapper::toResponse)
        .orElseThrow(
            () -> new ResourceNotFoundException("El estudiante no tiene ficha de contacto"));
  }

  // ---------- Utilidades ----------

  // Busca un estudiante por id o lanza ResourceNotFoundException.
  private Estudiante estudiante(Long id) {
    return estudiantes
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
  }

  // Lanza error 400 si la petición no trae cuerpo.
  private static void requireBody(Object request) {
    if (request == null) {
      throw new InvalidInputException("Cuerpo de la petición obligatorio");
    }
  }

  // Comprueba que un valor no sea null y lo devuelve; si es null lanza error 400.
  private static <T> T requerido(T valor, String campo) {
    if (valor == null) {
      throw new InvalidInputException(campo + " es obligatorio");
    }
    return valor;
  }

  // Valida un texto obligatorio: no nulo, no vacío y con longitud máxima. Devuelve el texto sin espacios.
  private static String texto(String valor, String campo, int max) {
    if (valor == null || valor.isBlank()) {
      throw new InvalidInputException(campo + " es obligatorio");
    }
    // Elimina espacios al principio y al final.
    String limpio = valor.trim();
    // Comprueba que no supere la longitud permitida por la columna.
    if (limpio.length() > max) {
      throw new InvalidInputException(campo + " supera " + max + " caracteres");
    }
    return limpio;
  }

  // Valida un email: texto obligatorio de hasta 200 caracteres y con formato correcto.
  private static String email(String valor, String campo) {
    String limpio = texto(valor, campo, 200);
    if (!EMAIL.matcher(limpio).matches()) {
      throw new InvalidInputException(campo + " no tiene un formato válido");
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
