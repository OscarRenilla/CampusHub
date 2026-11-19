// Paquete raíz de los tests de la aplicación.
package es.academia.campushub;

// Aserción para comprobar que el texto de la respuesta contiene un valor.
import static org.junit.jupiter.api.Assertions.assertTrue;

// Clase base con la API de pruebas HTTP y los contenedores de base de datos.
import es.academia.campushub.support.ApiIntegrationSupport;
// Anotación que marca un método como test de JUnit.
import org.junit.jupiter.api.Test;

/** Contrato visible de la API: flujos básicos de alta y consulta de principio a fin. */
class ContratoApiIT extends ApiIntegrationSupport {
  // Crea un departamento y un docente en él, y comprueba que se devuelve 201 con el email.
  @Test
  void registroDeDepartamentoYDocente() throws Exception {
    // Crea el departamento y guarda su id.
    long departamento = id(post("/api/departamentos", "{\"nombre\":\"Informática\"}"));
    // Crea el docente dentro de ese departamento.
    var docente =
        post(
            "/api/docentes",
            "{\"nombre\":\"Ana\",\"apellidos\":\"García\",\"email\":\"ana@campus.test\",\"departamentoId\":"
                + departamento
                + "}");
    // Debe responder 201 (Created).
    status(201, docente);
    // La respuesta debe incluir el email enviado.
    assertTrue(docente.body().contains("ana@campus.test"));
  }

  // Crea la cadena departamento -> docente -> grupo -> estudiante y recupera el estudiante.
  @Test
  void estudianteSeRecupera() throws Exception {
    // Crea el departamento.
    long dept = id(post("/api/departamentos", "{\"nombre\":\"Ciencias\"}"));
    // Crea el docente que será tutor.
    long docente =
        id(
            post(
                "/api/docentes",
                "{\"nombre\":\"Pedro\",\"apellidos\":\"Díaz\",\"email\":\"pedro@campus.test\",\"departamentoId\":"
                    + dept
                    + "}"));
    // Crea el grupo con ese tutor.
    long grupo =
        id(
            post(
                "/api/grupos",
                "{\"nombre\":\"DAM2\",\"nivel\":\"2 DAM\",\"tutorDocenteId\":" + docente + "}"));
    // Crea el estudiante en el grupo.
    var creado =
        post(
            "/api/estudiantes",
            "{\"nombre\":\"Laura\",\"apellidos\":\"Ruiz\",\"email\":\"laura@campus.test\",\"fechaNacimiento\":\"2004-04-01\",\"grupoId\":"
                + grupo
                + "}");
    // Debe responder 201 al crear.
    status(201, creado);
    // Al consultarlo por id debe responder 200.
    status(200, get("/api/estudiantes/" + id(creado)));
  }

  // Comprueba que una actividad admite seguimientos de estructura variable guardados en MongoDB.
  @Test
  void unaActividadAdmiteSeguimientosVariables() throws Exception {
    // Crea departamento y docente responsable.
    long dept = id(post("/api/departamentos", "{\"nombre\":\"Tecnología\"}"));
    long docente =
        id(
            post(
                "/api/docentes",
                "{\"nombre\":\"Iván\",\"apellidos\":\"López\",\"email\":\"ivan@campus.test\",\"departamentoId\":"
                    + dept
                    + "}"));
    // Crea la actividad.
    long actividad =
        id(
            post(
                "/api/actividades",
                "{\"titulo\":\"Visita\",\"descripcion\":\"Visita técnica\",\"tipo\":\"VISITA\",\"fechaPrevista\":\"2027-05-01\",\"maxParticipantes\":20,\"docenteId\":"
                    + docente
                    + "}"));
    // Registra un seguimiento con datos anidados (texto y lista); debe responder 201.
    status(
        201,
        post(
            "/api/actividades/" + actividad + "/seguimientos",
            "{\"tipo\":\"VISITA\",\"datos\":{\"empresa\":\"Acme\",\"observaciones\":[\"aprendizaje\",\"visita\"]}}"));
    // Consulta el detalle de la actividad.
    var detalle = get("/api/actividades/" + actividad + "/detalle");
    status(200, detalle);
    // El seguimiento guardado debe aparecer en el detalle.
    assertTrue(detalle.body().contains("Acme"));
  }
}
