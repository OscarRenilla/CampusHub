package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author Óscar Renilla
 * @author Marcel Abad
 * @author Álvaro Cotumba
 * @version 1.0
 */

/**
 * Punto de entrada de la tarea. Sustituye cada stub por una implementación real. NO hay entidades,
 * repositorios ni documentos entregados.
 */
@Service
public class CampusHubServiceImpl implements CampusHubService {
  private final OrganizacionService organizacionService;
  private final ActividadService actividadService;
  private final SeguimientoService seguimientoService;
  private final ActividadDetalleService actividadDetalleService;

  public CampusHubServiceImpl(
      OrganizacionService organizacionService,
      ActividadService actividadService,
      SeguimientoService seguimientoService,
      ActividadDetalleService actividadDetalleService) {
    this.organizacionService = organizacionService;
    this.actividadService = actividadService;
    this.seguimientoService = seguimientoService;
    this.actividadDetalleService = actividadDetalleService;
  }

  @Override
  public GrupoResponse crearGrupo(CreateGrupoRequest request) {
    return organizacionService.crearGrupo(request);
  }

  @Override
  public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request) {
    return organizacionService.crearDepartamento(request);
  }

  @Override
  public EstudianteResponse crearEstudiante(CreateEstudianteRequest request) {
    return organizacionService.crearEstudiante(request);
  }

  @Override
  public EstudianteResponse obtenerEstudiante(Long id) {
    return organizacionService.obtenerEstudiante(id);
  }

  @Override
  public DocenteResponse crearDocente(CreateDocenteRequest request) {
    return organizacionService.crearDocente(request);
  }

  @Override
  public ActividadResponse crearActividad(CreateActividadRequest request) {
    return actividadService.crearActividad(request);
  }

  @Override
  public ActividadResponse obtenerActividad(Long id) {
    return actividadService.obtenerActividad(id);
  }

  @Override
  public InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request) {
    return actividadService.inscribir(actividadId, request);
  }

  @Override
  public List<EstudianteResponse> participantes(Long actividadId) {
    return actividadService.participantes(actividadId);
  }

  //  @Override
  //  public SeguimientoResponse registrarSeguimiento(
  //      Long actividadId, CreateSeguimientoRequest request) {
  //    throw new PersistenciaPendienteException("registrarSeguimiento");
  //  }

  @Override
  public SeguimientoResponse registrarSeguimiento(
      Long actividadId, CreateSeguimientoRequest request) {
    return seguimientoService.registrarSeguimiento(actividadId, request);
  }

  @Override
  public ActividadDetalleResponse detalle(Long id) {
    return actividadDetalleService.detalle(id);
  }

  @Override
  public List<ActividadHistorialResponse> historial(Long estudianteId) {
    return actividadService.historial(estudianteId);
  }

  @Override
  public ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request) {
    return organizacionService.guardarContacto(estudianteId, request);
  }

  @Override
  public ContactoResponse contacto(Long estudianteId) {
    return organizacionService.contacto(estudianteId);
  }

  @Override
  public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request) {
    return actividadService.crearEtiqueta(request);
  }

  @Override
  public void asociarEtiqueta(Long actividadId, Long etiquetaId) {
    actividadService.asociarEtiqueta(actividadId, etiquetaId);
  }

  @Override
  public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
    return actividadService.actividadesPorEtiqueta(etiquetaId);
  }
}
