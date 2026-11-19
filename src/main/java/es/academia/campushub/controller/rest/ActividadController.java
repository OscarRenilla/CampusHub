// Paquete de los controladores REST (capa que recibe las peticiones HTTP).
package es.academia.campushub.controller.rest;

// Importa todos los DTO de petición y respuesta.
import es.academia.campushub.controller.dto.*;
// Servicio principal al que el controlador delega toda la lógica.
import es.academia.campushub.service.CampusHubService;
// Interfaz de lista para las respuestas con varios elementos.
import java.util.List;
// Enumeración con los códigos de estado HTTP.
import org.springframework.http.HttpStatus;
// Permite controlar el código de estado y el cuerpo de la respuesta.
import org.springframework.http.ResponseEntity;
// Anotaciones de Spring MVC (GetMapping, PostMapping, PathVariable...).
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints REST de las actividades: creación, consulta, inscripciones, seguimientos y detalle.
 * No contiene lógica de negocio: solo traduce HTTP a llamadas al servicio.
 */
// Clase controladora REST: los métodos devuelven datos (JSON) directamente.
@RestController
public class ActividadController {
  // Servicio al que se delega cada operación.
  private final CampusHubService service;

  // Constructor: Spring inyecta automáticamente el servicio.
  public ActividadController(CampusHubService service) {
    this.service = service;
  }

  // POST /api/actividades: crea una actividad a partir del JSON recibido.
  @PostMapping("/api/actividades")
  public ResponseEntity<ActividadResponse> crear(@RequestBody CreateActividadRequest r) {
    // Responde 201 (Created) con la actividad creada.
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crearActividad(r));
  }

  // GET /api/actividades/{id}: devuelve una actividad por su id.
  @GetMapping("/api/actividades/{id}")
  public ActividadResponse obtener(@PathVariable Long id) {
    return service.obtenerActividad(id);
  }

  // POST /api/actividades/{id}/inscripciones: inscribe un estudiante en la actividad.
  @PostMapping("/api/actividades/{id}/inscripciones")
  public ResponseEntity<InscripcionResponse> inscribir(
      @PathVariable Long id, @RequestBody CreateInscripcionRequest r) {
    // Responde 201 (Created) con la inscripción registrada.
    return ResponseEntity.status(HttpStatus.CREATED).body(service.inscribir(id, r));
  }

  // GET /api/actividades/{id}/inscripciones: lista los estudiantes inscritos.
  @GetMapping("/api/actividades/{id}/inscripciones")
  public List<EstudianteResponse> participantes(@PathVariable Long id) {
    return service.participantes(id);
  }

  // POST /api/actividades/{id}/seguimientos: registra un seguimiento (se guarda en MongoDB).
  @PostMapping("/api/actividades/{id}/seguimientos")
  public ResponseEntity<SeguimientoResponse> seguimiento(
      @PathVariable Long id, @RequestBody CreateSeguimientoRequest r) {
    // Responde 201 (Created) con el seguimiento guardado.
    return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarSeguimiento(id, r));
  }

  // GET /api/actividades/{id}/detalle: devuelve la información completa de la actividad.
  @GetMapping("/api/actividades/{id}/detalle")
  public ActividadDetalleResponse detalle(@PathVariable Long id) {
    return service.detalle(id);
  }
}
