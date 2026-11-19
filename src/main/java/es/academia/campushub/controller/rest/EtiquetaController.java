// Paquete de los controladores REST.
package es.academia.campushub.controller.rest;

// DTO que se usan en este controlador.
import es.academia.campushub.controller.dto.ActividadResponse;
import es.academia.campushub.controller.dto.CreateEtiquetaRequest;
import es.academia.campushub.controller.dto.EtiquetaResponse;
// Servicio principal al que se delega la lógica.
import es.academia.campushub.service.CampusHubService;
// Interfaz de lista.
import java.util.List;
// Códigos de estado HTTP.
import org.springframework.http.HttpStatus;
// Permite controlar estado y cuerpo de la respuesta.
import org.springframework.http.ResponseEntity;
// Anotaciones de Spring MVC.
import org.springframework.web.bind.annotation.*;

/** Endpoints REST de las etiquetas: crear, asociarlas a actividades y consultar por etiqueta. */
// Controlador REST (devuelve JSON).
@RestController
public class EtiquetaController {
  // Servicio al que se delega la lógica.
  private final CampusHubService service;

  // Constructor: Spring inyecta el servicio.
  public EtiquetaController(CampusHubService service) {
    this.service = service;
  }

  // POST /api/etiquetas: crea una etiqueta.
  @PostMapping("/api/etiquetas")
  public ResponseEntity<EtiquetaResponse> crear(@RequestBody CreateEtiquetaRequest r) {
    // Responde 201 (Created).
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crearEtiqueta(r));
  }

  // PUT /api/actividades/{id}/etiquetas/{etiquetaId}: asocia una etiqueta a una actividad.
  @PutMapping("/api/actividades/{id}/etiquetas/{etiquetaId}")
  public ResponseEntity<Void> asociar(@PathVariable Long id, @PathVariable Long etiquetaId) {
    service.asociarEtiqueta(id, etiquetaId);
    // Responde 204 (No Content): la operación fue correcta y no hay cuerpo.
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // GET /api/etiquetas/{id}/actividades: lista las actividades que tienen esa etiqueta.
  @GetMapping("/api/etiquetas/{id}/actividades")
  public List<ActividadResponse> actividades(@PathVariable Long id) {
    return service.actividadesPorEtiqueta(id);
  }
}
