package es.academia.campushub.service;

import es.academia.campushub.controller.dto.CreateSeguimientoRequest;
import es.academia.campushub.controller.dto.SeguimientoResponse;
import es.academia.campushub.document.Seguimiento;
import es.academia.campushub.repository.ActividadRepository;
import es.academia.campushub.repository.SeguimientoRepository;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * @author Óscar Renilla
 * @version 1.0
 */

/**
 * Implementación del servicio encargado de gestionar los seguimientos de las actividades.
 * <p>
 * Comprueba los datos recibidos, verifica la existencia de la actividad en PostgreSQL y almacena los seguimientos en MongoDB.
 */
@Service
public class SeguimientoServiceImpl implements SeguimientoService {

    // Repoditorio encargado de guardar y consultar seguimientos en MongoDB
    private final SeguimientoRepository seguimientoRepository;

    // Repositorio utilizado para comprobar la existencia de actividades en PostgreSQL
    private final ActividadRepository actividadRepository;

    /**
     * Constructor que recibe los repositorios necesarios mediante inyección de dependencias de Spring.
     *
     * @param seguimientoRepository repositorio de seguimientos
     * @param actividadRepository   repositorio de actividades
     */
    public SeguimientoServiceImpl(
            SeguimientoRepository seguimientoRepository, ActividadRepository actividadRepository) {
        this.seguimientoRepository = seguimientoRepository;
        this.actividadRepository = actividadRepository;
    }

    /**
     * Registra un nuevo seguimiento asociado a una actividad.
     * <p>
     * Primero valida los datos recibidos y comprueba que la actividad
     * existe. Después crea el documento, lo guarda en MongoDB y
     * convierte el resultado en un DTO de respuesta.
     *
     * @param actividadId identificador de la actividad
     * @param request     datos del seguimiento que se quiere registrar
     * @return información del seguimiento guardado
     */
    @Override
    public SeguimientoResponse registrarSeguimiento(
            Long actividadId, CreateSeguimientoRequest request) {

        // 1. Comprobar que el identificador de la actividad de la petición es válida
        if (actividadId == null || actividadId <= 0) {
            throw new InvalidInputException("El identificador de actividad no es válido");
        }

        // Comprobar que la peticion contiene un tipo y datos obligatorios.
        // Tambien se evita aceptar un tipo vacío o formado solo por espacios.
        if (request == null
                || request.tipo() == null
                || request.tipo().isBlank()
                || request.datos() == null) {
            throw new InvalidInputException("El tipo y los datos del seguimiento son obligatorios");
        }

        // 2. Comprobar que existe la actividad en PostgreSQL
        // Si no existe, se lanza una excepcion indicando que no se ha encontrado.
        if (!actividadRepository.existsById(actividadId)) {
            throw new ResourceNotFoundException("No existe la actividad con ID: " + actividadId);
        }

        // 3. Crear un nuevo documento Seguimiento para almacenarlo en MongoDB
        Seguimiento seguimiento = new Seguimiento();

        // Asociar el seguimiento con la actividad correspondiente
        seguimiento.setActividadId(actividadId);

        // Eliminar los espacios sobrantes del tipo de seguimiento
        seguimiento.setTipo(request.tipo().trim());

        // Copiar los datos recibidos en un nuevo mapa para almacenarlo
        seguimiento.setDatos(new LinkedHashMap<>(request.datos()));

        // Registrar la fecha y hora actuales
        seguimiento.setCreadoEn(LocalDateTime.now());

        // 4. Guardar el documento en MongoDB
        // Ek repsitorio devuelve el documento guardado, incluyendo su ID
        Seguimiento guardado = seguimientoRepository.save(seguimiento);

        // 5. Convertir el documento guardado en respuesta DTO para devolverlo
        return convertirAResponse(guardado);
    }

    /**
     * Obtiene los seguimientos de una actividad ordenados desde el mas reciente hasta el más antiguo.
     * <p>
     * Antes de consultar MongoDB, comprueba que el identificador es válido
     * y que la actividad existe en PostgreSQL.
     *
     * @param actividadId identificador de la actividad
     * @return lista de seguimientos convertidos a DTO
     */
    @Override
    public List<SeguimientoResponse> obtenerSeguimientos(Long actividadId) {

        // 1. Validar qu eel identificador de activiadad sea correcto
        if (actividadId == null || actividadId <= 0) {
            throw new InvalidInputException("El identificador de actividad no es válido");
        }

        // 2. Comprobar que existe la actividad en PostgrSQL
        if (!actividadRepository.existsById(actividadId)) {
            throw new ResourceNotFoundException("No existe la actividad con ID: " + actividadId);
        }

        // 3. Consultar MongoDB, ordenar los resultados por fecha descendente
        // y convertir cada documento en un DTO de respuesta.
        return seguimientoRepository.findByActividadIdOrderByCreadoEnDesc(actividadId).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    /**
     * Convierte un documento Seguimiento en un SeguimientoResponse.
     *
     * Permite separar la estructura interna del documento almacenado
     * en MongoDB de la información que recibe el cliente.
     *
     * @param seguimiento documento que se quiere convertir
     * @return DTO con los datos del seguimiento
     */
    private SeguimientoResponse convertirAResponse(Seguimiento seguimiento) {
        return new SeguimientoResponse(
                seguimiento.getId(),
                seguimiento.getActividadId(),
                seguimiento.getTipo(),
                seguimiento.getDatos(),
                seguimiento.getCreadoEn().toString());
    }
}
