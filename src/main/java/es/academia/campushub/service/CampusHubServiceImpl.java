// Paquete de la capa de servicios (lógica de negocio).
package es.academia.campushub.service;

// Importa todos los DTO de petición y respuesta.
import es.academia.campushub.controller.dto.*;

// Interfaz de lista.
import java.util.List;

// Marca la clase como servicio gestionado por Spring.
import org.springframework.stereotype.Service;

/**
 * Implementación principal del servicio de CampusHub.
 * <p>
 * Actúa como punto de entrada para las operaciones de la aplicación, delegando cada tarea en el
 * servicio especializado correspondiente. Centraliza el acceso a los servicios de organización
 * (Álvaro Cotumba), actividades (Marcel Abad), seguimientos y detalles de actividades (Óscar Renilla).
 *
 * @author Óscar Renilla
 * @author Marcel Abad
 * @author Álvaro Cotumba
 * @version 1.0
 */
@Service
public class CampusHubServiceImpl implements CampusHubService {

    // Servicio de grupos, departamentos, estudiantes, docentes y contactos. @author Álvaro Cotumba
    private final OrganizacionService organizacionService;

    // Servicio de actividades, inscripciones, participantes, historiales y etiquetas. @author Marcel Abad
    private final ActividadService actividadService;

    // Servicio de registro y consulta de seguimientos en MongoDB. @author Óscar Renilla
    private final SeguimientoService seguimientoService;

    // Servicio del detalle completo de una actividad. @author Óscar Renilla
    private final ActividadDetalleService actividadDetalleService;

    /**
     * Constructor que recibe los servicios necesarios mediante inyección de dependencias de Spring.
     *
     * @param organizacionService     servicio de organización
     * @param actividadService        servicio de actividades
     * @param seguimientoService      servicio de seguimientos
     * @param actividadDetalleService servicio de detalles de actividades
     */
    public CampusHubServiceImpl(
            OrganizacionService organizacionService,
            ActividadService actividadService,
            SeguimientoService seguimientoService,
            ActividadDetalleService actividadDetalleService) {
        // Guarda cada servicio recibido en su atributo.
        this.organizacionService = organizacionService;
        this.actividadService = actividadService;
        this.seguimientoService = seguimientoService;
        this.actividadDetalleService = actividadDetalleService;
    }

    // Delega la creación de un grupo en el servicio de organización.
    @Override
    public GrupoResponse crearGrupo(CreateGrupoRequest request) {
        return organizacionService.crearGrupo(request);
    }

    // Delega la creación de un departamento en el servicio de organización.
    @Override
    public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request) {
        return organizacionService.crearDepartamento(request);
    }

    // Delega la creación de un estudiante en el servicio de organización.
    @Override
    public EstudianteResponse crearEstudiante(CreateEstudianteRequest request) {
        return organizacionService.crearEstudiante(request);
    }

    // Obtiene los datos de un estudiante mediante su identificador.
    @Override
    public EstudianteResponse obtenerEstudiante(Long id) {
        return organizacionService.obtenerEstudiante(id);
    }

    // Delega la creación de un docente en el servicio de organización.
    @Override
    public DocenteResponse crearDocente(CreateDocenteRequest request) {
        return organizacionService.crearDocente(request);
    }

    // Delega la creación de una actividad en el servicio de actividades.
    @Override
    public ActividadResponse crearActividad(CreateActividadRequest request) {
        return actividadService.crearActividad(request);
    }

    // Obtiene los datos de una actividad mediante su identificador.
    @Override
    public ActividadResponse obtenerActividad(Long id) {
        return actividadService.obtenerActividad(id);
    }

    // Inscribe a un estudiante en una actividad.
    @Override
    public InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request) {
        return actividadService.inscribir(actividadId, request);
    }

    // Obtiene la lista de estudiantes inscritos en una actividad.
    @Override
    public List<EstudianteResponse> participantes(Long actividadId) {
        return actividadService.participantes(actividadId);
    }

    // Versión antigua (stub de la plantilla) que lanzaba PersistenciaPendienteException; se dejó
    // comentada porque ahora la operación está implementada en SeguimientoService.
    //  @Override
    //  public SeguimientoResponse registrarSeguimiento(
    //      Long actividadId, CreateSeguimientoRequest request) {
    //    throw new PersistenciaPendienteException("registrarSeguimiento");
    //  }

    // Registra un seguimiento delegando la operación en SeguimientoService.
    @Override
    public SeguimientoResponse registrarSeguimiento(
            Long actividadId, CreateSeguimientoRequest request) {
        return seguimientoService.registrarSeguimiento(actividadId, request);
    }

    // Obtiene el detalle completo de una actividad.
    @Override
    public ActividadDetalleResponse detalle(Long id) {
        return actividadDetalleService.detalle(id);
    }

    // Obtiene el historial de actividades de un estudiante.
    @Override
    public List<ActividadHistorialResponse> historial(Long estudianteId) {
        return actividadService.historial(estudianteId);
    }

    // Guarda o actualiza la información de contacto de un estudiante.
    @Override
    public ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request) {
        return organizacionService.guardarContacto(estudianteId, request);
    }

    // Obtiene la información de contacto de un estudiante.
    @Override
    public ContactoResponse contacto(Long estudianteId) {
        return organizacionService.contacto(estudianteId);
    }

    // Crea una nueva etiqueta para clasificar actividades.
    @Override
    public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request) {
        return actividadService.crearEtiqueta(request);
    }

    // Asocia una etiqueta existente a una actividad.
    @Override
    public void asociarEtiqueta(Long actividadId, Long etiquetaId) {
        actividadService.asociarEtiqueta(actividadId, etiquetaId);
    }

    // Busca las actividades relacionadas con una etiqueta.
    @Override
    public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
        return actividadService.actividadesPorEtiqueta(etiquetaId);
    }
}
