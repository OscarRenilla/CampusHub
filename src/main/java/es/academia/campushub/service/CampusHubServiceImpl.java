package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.util.exception.PersistenciaPendienteException;
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
  @Override
  public GrupoResponse crearGrupo(CreateGrupoRequest request) {
    throw new PersistenciaPendienteException("crearGrupo");
  }

  @Override
  public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request) {
    throw new PersistenciaPendienteException("crearDepartamento");
  }

  @Override
  public EstudianteResponse crearEstudiante(CreateEstudianteRequest request) {
    throw new PersistenciaPendienteException("crearEstudiante");
  }

  @Override
  public EstudianteResponse obtenerEstudiante(Long id) {
    throw new PersistenciaPendienteException("obtenerEstudiante");
  }

  @Override
  public DocenteResponse crearDocente(CreateDocenteRequest request) {
    throw new PersistenciaPendienteException("crearDocente");
  }

  @Override
  public ActividadResponse crearActividad(CreateActividadRequest request) {
    throw new PersistenciaPendienteException("crearActividad");
  }

  @Override
  public ActividadResponse obtenerActividad(Long id) {
    throw new PersistenciaPendienteException("obtenerActividad");
  }

  @Override
  public InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request) {
    throw new PersistenciaPendienteException("inscribir");
  }

  @Override
  public List<EstudianteResponse> participantes(Long actividadId) {
    throw new PersistenciaPendienteException("participantes");
  }

  @Override
  public SeguimientoResponse registrarSeguimiento(
      Long actividadId, CreateSeguimientoRequest request) {
    throw new PersistenciaPendienteException("registrarSeguimiento");
  }

  @Override
  public ActividadDetalleResponse detalle(Long id) {
    throw new PersistenciaPendienteException("detalle");
  }

  @Override
  public List<ActividadHistorialResponse> historial(Long estudianteId) {
    throw new PersistenciaPendienteException("historial");
  }

  @Override
  public ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request) {
    throw new PersistenciaPendienteException("guardarContacto");
  }

  @Override
  public ContactoResponse contacto(Long estudianteId) {
    throw new PersistenciaPendienteException("contacto");
  }

  @Override
  public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request) {
    throw new PersistenciaPendienteException("crearEtiqueta");
  }

  @Override
  public void asociarEtiqueta(Long actividadId, Long etiquetaId) {
    throw new PersistenciaPendienteException("asociarEtiqueta");
  }

  @Override
  public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
    throw new PersistenciaPendienteException("actividadesPorEtiqueta");
  }
}
