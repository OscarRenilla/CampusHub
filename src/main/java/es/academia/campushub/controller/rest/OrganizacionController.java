// Paquete de los controladores REST.
package es.academia.campushub.controller.rest;

// Importa todos los DTO de petición y respuesta.
import es.academia.campushub.controller.dto.*;
// Servicio principal al que se delega la lógica.
import es.academia.campushub.service.CampusHubService;
// Códigos de estado HTTP.
import org.springframework.http.HttpStatus;
// Permite controlar estado y cuerpo de la respuesta.
import org.springframework.http.ResponseEntity;
// Anotación para mapear peticiones POST.
import org.springframework.web.bind.annotation.PostMapping;
// Anotación que convierte el cuerpo JSON en un objeto Java.
import org.springframework.web.bind.annotation.RequestBody;
// Marca la clase como controlador REST.
import org.springframework.web.bind.annotation.RestController;

/** Endpoints REST de la estructura del centro: grupos, departamentos y docentes. */
// Controlador REST (devuelve JSON).
@RestController
public class OrganizacionController {
  // Servicio al que se delega la lógica.
  private final CampusHubService service;

  // Constructor: Spring inyecta el servicio.
  public OrganizacionController(CampusHubService service) {
    this.service = service;
  }

  // POST /api/grupos: crea un grupo con su tutor.
  @PostMapping("/api/grupos")
  public ResponseEntity<GrupoResponse> grupo(@RequestBody CreateGrupoRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crearGrupo(r));
  }

  // POST /api/departamentos: crea un departamento.
  @PostMapping("/api/departamentos")
  public ResponseEntity<DepartamentoResponse> departamento(
      @RequestBody CreateDepartamentoRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crearDepartamento(r));
  }

  // POST /api/docentes: crea un docente dentro de un departamento.
  @PostMapping("/api/docentes")
  public ResponseEntity<DocenteResponse> docente(@RequestBody CreateDocenteRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.crearDocente(r));
  }
}
