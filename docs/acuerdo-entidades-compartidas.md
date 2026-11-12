# Acuerdo de nombres de entidades compartidas - CampusHub

Este documento fija el acuerdo de nombres. Se aplica a las entidades JPA y sus repositorios; no modifica los DTO ni los endpoints públicos.

## Convenciones comunes

- Paquetes: `es.academia.campushub.entity` y `es.academia.campushub.repository`.
- Clases Java en singular y `UpperCamelCase`; propiedades en `lowerCamelCase`.
- Clave primaria: `id` de tipo `Long`.
- Relaciones JPA representadas mediante propiedades de entidad, no mediante propiedades `...Id` de tipo `Long`. Los `...Id` pertenecen al contrato HTTP/DTO.
- Usar `@ManyToOne` como lado propietario de las relaciones N:1; `@OneToMany(mappedBy = "...")` en el lado inverso.
- Mantener los nombres de propiedades y `mappedBy` exactamente como se indican aquí.

## Entidades y propiedades acordadas

### Álvaro - organización y estudiantes

| Clase | Propiedades y relaciones |
|---|---|
| `Departamento` | `id`, `nombre`, `docentes` (`@OneToMany(mappedBy = "departamento")`) |
| `Docente` | `id`, `nombre`, `apellidos`, `email`, `departamento` (`@ManyToOne`) |
| `Grupo` | `id`, `nombre`, `nivel`, `tutor` (`@ManyToOne` hacia `Docente`), `estudiantes` (`@OneToMany(mappedBy = "grupo")`) |
| `Estudiante` | `id`, `nombre`, `apellidos`, `email`, `fechaNacimiento`, `grupo` (`@ManyToOne`), `contacto` (`@OneToOne(mappedBy = "estudiante")`), `inscripciones` (`@OneToMany(mappedBy = "estudiante")`) |
| `ContactoEstudiante` | `id`, `estudiante` (`@OneToOne`, lado propietario con FK única y NOT NULL), `tutorLegal`, `telefono`, `email`, `autorizado`, `fechaAutorizacion` |

Restricciones: `Departamento.nombre` único y NOT NULL; `Docente.email` y `Estudiante.email` únicos y NOT NULL. Las FK de `Docente.departamento`, `Grupo.tutor`, `Estudiante.grupo` y `ContactoEstudiante.estudiante` deben referenciar registros existentes y ser NOT NULL, conforme a los requisitos del modelo.

### Marcel - actividades, inscripciones y etiquetas

| Clase | Propiedades y relaciones |
|---|---|
| `Actividad` | `id`, `titulo`, `descripcion`, `tipo`, `fechaPrevista`, `maxParticipantes`, `docente` (`@ManyToOne`), `inscripciones` (`@OneToMany(mappedBy = "actividad")`), `etiquetas` (`@ManyToMany`) |
| `Inscripcion` | `id`, `actividad` (`@ManyToOne`), `estudiante` (`@ManyToOne`), `fechaInscripcion` |
| `Etiqueta` | `id`, `nombre`, `actividades` (`@ManyToMany(mappedBy = "etiquetas")`) |

Restricciones: `UNIQUE(actividad_id, estudiante_id)` en `inscripciones`; `CHECK (max_participantes > 0)` en `actividades`; `Etiqueta.nombre` único y NOT NULL. La tabla intermedia N:M se llamará `actividad_etiqueta`, con FK `actividad_id` y `etiqueta_id` y clave única compuesta por ambas.

## Repositorios acordados

Todos extienden `JpaRepository<Entidad, Long>` y se ubican en `es.academia.campushub.repository`:

- `DepartamentoRepository`
- `DocenteRepository`
- `GrupoRepository`
- `EstudianteRepository`
- `ContactoEstudianteRepository`
- `ActividadRepository`
- `InscripcionRepository`
- `EtiquetaRepository`

## Contrato 

1. Álvaro crea primero `Docente` y `Estudiante` con los nombres y relaciones anteriores y los sube a la rama compartida.
2. Marcel referencia esas clases mediante `Actividad.docente` e `Inscripcion.estudiante`; no crea copias ni entidades alternativas.
3. Los campos `tutorDocenteId`, `grupoId`, `departamentoId`, `docenteId` y `estudianteId` siguen existiendo únicamente en los DTO/requests. En las entidades se usan `tutor`, `grupo`, `departamento`, `docente` y `estudiante`.
4. No se cambian controllers ni DTO para adaptar nombres internos.
5. Si se necesita cambiar este acuerdo, deben aprobarlo todos los integrantes del equipo antes de modificar código y actualizar este documento en el mismo PR.

## Correspondencia con el contrato HTTP

Los nombres anteriores son internos. Se mantienen sin cambios los DTO actuales: `CreateDocenteRequest.departamentoId`, `CreateGrupoRequest.tutorDocenteId`, `CreateEstudianteRequest.grupoId`, `CreateActividadRequest.docenteId` y `CreateInscripcionRequest.estudianteId`. Los mappers/servicios resolverán esos identificadores a sus entidades correspondientes.
---

Persona A: Álvaro
Persona B: Marcel
Persona C: Óscar 
