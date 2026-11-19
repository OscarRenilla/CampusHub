// Paquete raíz de los tests de la aplicación.
package es.academia.campushub;

// Aserción para comprobar que el texto de la respuesta contiene un valor.
import static org.junit.jupiter.api.Assertions.assertTrue;

// Clase base con la API de pruebas HTTP y los contenedores de base de datos.
import es.academia.campushub.support.ApiIntegrationSupport;
// Anotación que marca un método como test de JUnit.
import org.junit.jupiter.api.Test;

/** Pruebas visibles de relaciones e integridad de la API. */
class RelacionesApiIT extends ApiIntegrationSupport {
  // Comprueba que no se puede inscribir dos veces al mismo estudiante ni superar el aforo.
  @Test
  void inscripcionUnicaYAforo() throws Exception {
    // Prepara departamento, docente y grupo.
    long dept = id(post("/api/departamentos", "{\"nombre\":\"DepartamentoUno\"}"));
    long docente =
        id(
            post(
                "/api/docentes",
                "{\"nombre\":\"Rosa\",\"apellidos\":\"Gómez\",\"email\":\"rosa.inscripciones@test.invalid\",\"departamentoId\":"
                    + dept
                    + "}"));
    long grupo =
        id(
            post(
                "/api/grupos",
                "{\"nombre\":\"DAM1-A\",\"nivel\":\"1 DAM\",\"tutorDocenteId\":" + docente + "}"));
    // Crea dos estudiantes distintos.
    long e1 =
        id(
            post(
                "/api/estudiantes",
                "{\"nombre\":\"Eva\",\"apellidos\":\"Uno\",\"email\":\"eva1@test.invalid\",\"fechaNacimiento\":\"2005-01-01\",\"grupoId\":"
                    + grupo
                    + "}"));
    long e2 =
        id(
            post(
                "/api/estudiantes",
                "{\"nombre\":\"Eva\",\"apellidos\":\"Dos\",\"email\":\"eva2@test.invalid\",\"fechaNacimiento\":\"2005-01-02\",\"grupoId\":"
                    + grupo
                    + "}"));
    // Crea una actividad con aforo de una sola plaza.
    long actividad =
        id(
            post(
                "/api/actividades",
                "{\"titulo\":\"Laboratorio\",\"descripcion\":\"Práctica\",\"tipo\":\"TALLER\",\"fechaPrevista\":\"2027-07-01\",\"maxParticipantes\":1,\"docenteId\":"
                    + docente
                    + "}"));
    // La primera inscripción es correcta (201).
    status(
        201,
        post("/api/actividades/" + actividad + "/inscripciones", "{\"estudianteId\":" + e1 + "}"));
    // Repetir la inscripción del mismo estudiante da conflicto (409).
    status(
        409,
        post("/api/actividades/" + actividad + "/inscripciones", "{\"estudianteId\":" + e1 + "}"));
    // Un segundo estudiante no cabe porque el aforo está completo (409).
    status(
        409,
        post("/api/actividades/" + actividad + "/inscripciones", "{\"estudianteId\":" + e2 + "}"));
    // El detalle debe indicar que no quedan plazas.
    var detalle = get("/api/actividades/" + actividad + "/detalle");
    status(200, detalle);
    assertTrue(detalle.body().contains("\"plazasDisponibles\":0"), detalle.body());
    // El historial del estudiante inscrito debe responder 200.
    status(200, get("/api/estudiantes/" + e1 + "/actividades"));
  }

  // Comprueba la ficha de contacto 1:1 y la relación N:M entre actividades y etiquetas.
  @Test
  void fichaYRelacionDeEtiquetas() throws Exception {
    // Prepara departamento, docente, grupo y estudiante.
    long dept = id(post("/api/departamentos", "{\"nombre\":\"DepartamentoDos\"}"));
    long docente =
        id(
            post(
                "/api/docentes",
                "{\"nombre\":\"Carlos\",\"apellidos\":\"López\",\"email\":\"carlos.contacto@test.invalid\",\"departamentoId\":"
                    + dept
                    + "}"));
    long grupo =
        id(
            post(
                "/api/grupos",
                "{\"nombre\":\"DAM2-B\",\"nivel\":\"2 DAM\",\"tutorDocenteId\":" + docente + "}"));
    long estudiante =
        id(
            post(
                "/api/estudiantes",
                "{\"nombre\":\"Lola\",\"apellidos\":\"Ortiz\",\"email\":\"lola@test.invalid\",\"fechaNacimiento\":\"2004-01-01\",\"grupoId\":"
                    + grupo
                    + "}"));
    // Guarda la ficha de contacto del estudiante (PUT = upsert); responde 200.
    status(
        200,
        put(
            "/api/estudiantes/" + estudiante + "/contacto",
            "{\"tutorLegal\":\"Julia Ortiz\",\"telefono\":\"600000000\",\"email\":\"julia@test.invalid\",\"autorizado\":true,\"fechaAutorizacion\":\"2026-10-01\"}"));
    // Recupera la ficha y comprueba que contiene el tutor legal.
    var contacto = get("/api/estudiantes/" + estudiante + "/contacto");
    status(200, contacto);
    assertTrue(contacto.body().contains("Julia Ortiz"));
    // Crea una actividad y una etiqueta.
    long actividad =
        id(
            post(
                "/api/actividades",
                "{\"titulo\":\"Seguridad\",\"descripcion\":\"Charla\",\"tipo\":\"CHARLA\",\"fechaPrevista\":\"2027-06-01\",\"maxParticipantes\":10,\"docenteId\":"
                    + docente
                    + "}"));
    long etiqueta = id(post("/api/etiquetas", "{\"nombre\":\"seguridad-prueba-publica\"}"));
    // Asocia la etiqueta a la actividad; responde 204 (sin contenido).
    status(204, put("/api/actividades/" + actividad + "/etiquetas/" + etiqueta, null));
    // Lista las actividades de la etiqueta y comprueba que aparece la actividad creada.
    var lista = get("/api/etiquetas/" + etiqueta + "/actividades");
    status(200, lista);
    assertTrue(lista.body().contains("Seguridad"), lista.body());
  }
}
