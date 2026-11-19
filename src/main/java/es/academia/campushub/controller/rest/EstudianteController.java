// Paquete de los controladores REST.
package es.academia.campushub.controller.rest;

// Importa todos los DTO de petición y respuesta.
import es.academia.campushub.controller.dto.*;
// Servicio principal al que se delega la lógica.
import es.academia.campushub.service.CampusHubService;
// Interfaz de lista.
import java.util.List;
// Permite controlar estado y cuerpo de la respuesta.
import org.springframework.http.ResponseEntity;
// Anotaciones de Spring MVC.
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints REST de los estudiantes: alta, consulta, historial de actividades y ficha de contacto.
 */
// Controlador REST (devuelve JSON).
@RestController
public class EstudianteController {
  // Servicio de casos de uso (abreviado como uc).
  private final CampusHubService uc;

  // Constructor: Spring inyecta el servicio.
  public EstudianteController(CampusHubService uc) {
    this.uc = uc;
  }

  // POST /api/estudiantes: crea un estudiante.
  @PostMapping("/api/estudiantes")
  public ResponseEntity<EstudianteResponse> crear(@RequestBody CreateEstudianteRequest r) {
    // Responde 201 (Created) con el estudiante creado.
    return ResponseEntity.status(201).body(uc.crearEstudiante(r));
  }

  // GET /api/estudiantes/{id}: devuelve un estudiante.
  @GetMapping("/api/estudiantes/{id}")
  public EstudianteResponse obtener(@PathVariable Long id) {
    return uc.obtenerEstudiante(id);
  }

  // GET /api/estudiantes/{id}/actividades: historial de actividades en las que está inscrito.
  @GetMapping("/api/estudiantes/{id}/actividades")
  public List<ActividadHistorialResponse> historial(@PathVariable Long id) {
    return uc.historial(id);
  }

  // PUT /api/estudiantes/{id}/contacto: crea o actualiza la ficha de contacto (upsert).
  @PutMapping("/api/estudiantes/{id}/contacto")
  public ContactoResponse contactoGuardar(
      @PathVariable Long id, @RequestBody UpsertContactoRequest r) {
    return uc.guardarContacto(id, r);
  }

  // GET /api/estudiantes/{id}/contacto: devuelve la ficha de contacto.
  @GetMapping("/api/estudiantes/{id}/contacto")
  public ContactoResponse contacto(@PathVariable Long id) {
    return uc.contacto(id);
  }
}
