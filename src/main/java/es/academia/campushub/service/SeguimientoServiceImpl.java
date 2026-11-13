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

// Import temporal para pruebas:
import org.springframework.data.mongodb.core.MongoTemplate;

@Service
public class SeguimientoServiceImpl implements SeguimientoService {

  private final SeguimientoRepository seguimientoRepository;
  private final ActividadRepository actividadRepository;

  // Dependencia temporal
  private final MongoTemplate mongoTemplate;

  public SeguimientoServiceImpl(
          SeguimientoRepository seguimientoRepository,
          ActividadRepository actividadRepository,
          MongoTemplate mongoTemplate) {

    this.seguimientoRepository = seguimientoRepository;
    this.actividadRepository = actividadRepository;
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public SeguimientoResponse registrarSeguimiento(
      Long actividadId, CreateSeguimientoRequest request) {

    // 1. Comprobar que la petición es válida
    if (actividadId == null || actividadId <= 0) {
      throw new InvalidInputException("El identificador de actividad no es válido");
    }

    if (request == null
        || request.tipo() == null
        || request.tipo().isBlank()
        || request.datos() == null) {
      throw new InvalidInputException("El tipo y los datos del seguimiento son obligatorios");
    }

    // 2. Comprobar que existe la actividad en PostgreSQL
    if (!actividadRepository.existsById(actividadId)) {
      throw new ResourceNotFoundException("No existe la actividad con ID: " + actividadId);
    }

    // 3. Crear el documento para MongoDB
    Seguimiento seguimiento = new Seguimiento();

    seguimiento.setActividadId(actividadId);
    seguimiento.setTipo(request.tipo().trim());
    seguimiento.setDatos(new LinkedHashMap<>(request.datos()));
    seguimiento.setCreadoEn(LocalDateTime.now());

    // Código temporal para comporbaciones
    System.out.println(">>> EJECUTANDO GUARDADO EN MONGODB <<<");
    System.out.println(">>> BASE DE DATOS MONGO: " +
            mongoTemplate.getDb().getName());
    System.out.println(">>> COLECCIONES MONGO: " +
            mongoTemplate.getCollectionNames());

    // 4. Guardar el documento en MongoDB
    Seguimiento guardado = seguimientoRepository.save(seguimiento);

    // Fragmento de código temporal para comporbaciones
    System.out.println(">>> ID GUARDADO EN MONGO: " + guardado.getId());
    System.out.println(">>> DOCUMENTO RECUPERADO: " +
            seguimientoRepository.findById(guardado.getId()).orElse(null));

    // 5. Convertir el documento en respuesta DTO
    return convertirAResponse(guardado);
  }

  @Override
  public List<SeguimientoResponse> obtenerSeguimientos(Long actividadId) {

    // 1. Validar identificador
    if (actividadId == null || actividadId <= 0) {
      throw new InvalidInputException("El identificador de actividad no es válido");
    }

    // 2. Comprobar que existe la actividad
    if (!actividadRepository.existsById(actividadId)) {
      throw new ResourceNotFoundException("No existe la actividad con ID: " + actividadId);
    }

    // 3. Consultar MongoDB y convertir los resultados
    return seguimientoRepository.findByActividadIdOrderByCreadoEnDesc(actividadId).stream()
        .map(this::convertirAResponse)
        .toList();
  }

  private SeguimientoResponse convertirAResponse(Seguimiento seguimiento) {
    return new SeguimientoResponse(
        seguimiento.getId(),
        seguimiento.getActividadId(),
        seguimiento.getTipo(),
        seguimiento.getDatos(),
        seguimiento.getCreadoEn().toString());
  }
}
