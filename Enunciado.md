# Actividad evaluable: CampusHub

---

# 1. Contexto del proyecto

Un grupo de centros educativos ha decidido desarrollar una aplicación denominada **CampusHub**, cuyo objetivo es gestionar las actividades complementarias que organizan a lo largo del curso académico.

Actualmente, cada centro utiliza diferentes herramientas: hojas de cálculo para las inscripciones, documentos compartidos para registrar incidencias y archivos independientes para almacenar los resultados de las actividades.

Esta situación provoca algunos problemas:

- Existen estudiantes inscritos varias veces en una misma actividad.
- En ocasiones se supera el número de plazas disponibles.
- Resulta complicado consultar las actividades en las que ha participado cada estudiante.
- La información generada durante una actividad no siempre tiene el mismo formato.
- El personal necesita consultar el historial de actividades y recuperar la información registrada anteriormente.
- Algunas consultas se repiten continuamente y podrían optimizarse.

Para solucionarlo, se ha comenzado a desarrollar una **API REST con Spring Boot**.

El equipo anterior ha preparado la infraestructura, los controllers, los DTO y parte de las pruebas, pero **todavía no se ha diseñado ni implementado la persistencia de datos**.

Vuestra pareja será responsable de analizar los requisitos, diseñar el almacenamiento e implementar los componentes necesarios para que la aplicación funcione.

El proyecto dispone de dos sistemas de bases de datos: **PostgreSQL y MongoDB**.

> **No se ha decidido qué información debe almacenarse en cada uno. Esa decisión es responsabilidad vuestra.**

---

# 2. Objetivo de la actividad

El objetivo principal no es simplemente conseguir que una API funcione.

Debéis demostrar que sois capaces de:

1. Interpretar requisitos funcionales y transformarlos en un modelo de datos.
2. Identificar la información que necesita persistir una aplicación.
3. Diseñar relaciones, identificadores, restricciones e índices.
4. Seleccionar una tecnología de almacenamiento adecuada según las características de los datos.
5. Implementar persistencia relacional utilizando JPA e Hibernate.
6. Implementar persistencia documental utilizando Spring Data MongoDB.
7. Recuperar, relacionar y combinar información almacenada en sistemas diferentes.
8. Comprobar el funcionamiento mediante pruebas automatizadas.
9. Explicar y defender las decisiones técnicas adoptadas.

No existe necesariamente una única solución correcta.

Se valorará tanto el funcionamiento como la calidad de las decisiones de diseño.

---

# 3. Requisitos funcionales

Todos los requisitos de esta sección son obligatorios. La única ampliación voluntaria es Redis, descrita en la sección 11.

Los campos exactos de entrada y salida están definidos por los DTO y por el contrato incluido en el proyecto inicial.

## RF01. Gestión de grupos y estudiantes

El sistema debe permitir crear grupos y registrar estudiantes que participarán en las actividades.

Cada grupo dispone de:

- Nombre.
- Nivel.
- Docente tutor.

De cada estudiante se necesita conocer:

- Nombre.
- Apellidos.
- Correo electrónico.
- Fecha de nacimiento.
- Grupo al que pertenece.

El correo electrónico debe identificar de forma única a un estudiante dentro del sistema.

El sistema debe permitir registrar y recuperar estudiantes. No se podrán crear estudiantes duplicados ni asociarlos a grupos inexistentes.

**Consideraciones:**

- ¿Cómo se representa la relación entre grupo y estudiantes?
- ¿Cómo se garantiza que no existan dos estudiantes con el mismo correo?
- ¿Qué ocurre cuando se intenta registrar un estudiante duplicado?
- ¿Qué debería suceder si el grupo indicado no existe?

## RF02. Gestión de departamentos y docentes

Las actividades son organizadas por docentes del centro.

Cada departamento tiene un nombre único y agrupa a varios docentes.

De cada docente debe registrarse:

- Nombre.
- Apellidos.
- Correo electrónico.
- Departamento al que pertenece.

El correo del docente debe ser único.

Un docente puede responsabilizarse de distintas actividades a lo largo del curso. Cada actividad debe tener un docente responsable existente.

## RF03. Organización de actividades

El centro organiza diferentes tipos de actividades:

- Talleres prácticos.
- Charlas.
- Visitas a empresas.
- Jornadas tecnológicas.

Cada actividad tiene como mínimo:

- Título.
- Descripción.
- Tipo de actividad.
- Fecha prevista.
- Número máximo de participantes.
- Docente responsable.

El número máximo de participantes debe ser mayor que cero.

Una actividad puede existir aunque todavía no tenga estudiantes inscritos.

El sistema debe permitir crear actividades y consultar su información.

## RF04. Inscripciones

Los estudiantes pueden inscribirse en las actividades.

Deben cumplirse las siguientes condiciones:

1. Un estudiante puede participar en diferentes actividades.
2. Una actividad puede contar con numerosos estudiantes.
3. No se permite que un estudiante se inscriba dos veces en la misma actividad.
4. No se puede realizar una inscripción cuando se ha alcanzado el número máximo de plazas.
5. Debe conservarse la fecha en que se realizó la inscripción.
6. El sistema debe permitir consultar los estudiantes inscritos en una actividad.

| 🚨 | **Importante** <br/>No basta con comprobar estas reglas mediante validaciones en memoria. Debéis utilizar mecanismos de persistencia que protejan la información frente a duplicados, referencias inválidas y operaciones concurrentes. |
| --- | --- |

## RF05. Registro de seguimiento de las actividades

Una vez realizadas las actividades, el personal del centro necesita almacenar información sobre lo sucedido.

Sin embargo, no todas las actividades producen el mismo tipo de información.

### Taller de programación

Se puede registrar el lenguaje utilizado, las herramientas empleadas, los ejercicios completados y las observaciones.

### Visita a una empresa

Se puede registrar la empresa visitada, las personas que atendieron al grupo, las observaciones y las incidencias.

### Charla tecnológica

Se puede registrar el ponente, los temas tratados, los enlaces a recursos y las preguntas realizadas.

Además:

- Una actividad puede generar varios registros en momentos diferentes.
- Cada registro debe conservar su fecha de creación.
- Debe conservarse exactamente la estructura de los datos recibidos.
- Pueden aparecer nuevos tipos de seguimiento en el futuro.
- Los registros históricos deben seguir siendo recuperables aunque evolucionen los formatos de la aplicación.

> **Vuestro reto consiste en diseñar cómo representar y almacenar información cuya estructura puede variar con el tiempo.**
>

## RF06. Consulta del detalle de una actividad

El centro necesita consultar la información completa de una actividad desde una única operación de la API.

La respuesta debe incluir:

- Información general de la actividad.
- Docente responsable.
- Número de plazas ocupadas.
- Número de plazas disponibles.
- Estudiantes inscritos.
- Etiquetas asociadas.
- Registros de seguimiento.

Las colecciones sin elementos deben devolverse como listas vacías, nunca como `null`.

La API debe ofrecer una respuesta coherente independientemente de cómo y dónde se hayan almacenado los datos.

Si la información procede de sistemas diferentes, será responsabilidad vuestra recuperarla y combinarla desde la capa de servicio.

## RF07. Conservación y consistencia

Los datos registrados deben conservarse aunque se detenga y reinicie la aplicación Spring Boot.

Además:

- No deben existir inscripciones que hagan referencia a estudiantes o actividades inexistentes.
- No deben perderse datos al recuperar información de seguimiento.
- Las operaciones deben devolver resultados consistentes.
- Debéis identificar los problemas de integridad que aparecen al utilizar diferentes sistemas de almacenamiento.

No será obligatorio implementar transacciones distribuidas, pero sí explicar cómo puede aparecer una inconsistencia entre PostgreSQL y MongoDB y cómo reduciríais ese riesgo.

## RF08. Historial de actividades por estudiante

El sistema debe permitir consultar las actividades en las que participa un estudiante.

Cada elemento del historial incluirá los datos definidos por `ActividadHistorialResponse`, entre ellos:

- Identificador y título de la actividad.
- Tipo de actividad.
- Fecha prevista.
- Fecha de inscripción.
- Docente responsable.

## RF09. Ficha de contacto del estudiante

Cada estudiante puede tener como máximo una ficha de contacto con:

- Nombre del tutor legal.
- Teléfono.
- Correo electrónico.
- Indicación de si autoriza la participación en actividades fuera del centro.
- Fecha de autorización.

La ficha no puede existir sin el estudiante.

La operación `PUT` debe crear o actualizar la ficha y la operación `GET` debe permitir recuperarla.

## RF10. Etiquetas de las actividades

Las actividades pueden clasificarse mediante etiquetas o competencias, por ejemplo, `programación`, `empresa` o `seguridad`.

Una actividad puede tener varias etiquetas y una misma etiqueta puede aplicarse a muchas actividades.

Debe cumplirse lo siguiente:

- El nombre de una etiqueta no debe duplicarse.
- Asociar dos veces la misma etiqueta a una actividad no debe duplicar la relación.
- Debe poder consultarse qué actividades tienen una etiqueta determinada.
- El detalle de la actividad debe incluir sus etiquetas.

---

## Resumen de RF y endpoints

- RF01: crear grupo, registrar estudiante y consultar estudiante.
- RF02: crear departamento y registrar docente.
- RF03: crear y consultar actividad.
- RF04: inscribir estudiante y consultar participantes.
- RF05: registrar seguimiento.
- RF06: consultar detalle completo.
- RF07: no tiene endpoint propio; es una cualidad transversal que afecta a toda la persistencia.
- RF08: consultar historial.
- RF09: guardar y consultar contacto.
- RF10: crear etiqueta, asociarla y consultar actividades por etiqueta.

# 4. El reto principal: diseñar la persistencia

Disponéis de **PostgreSQL y MongoDB**.

Ambas tecnologías deben utilizarse de forma real en el proyecto, pero **no se os indicará qué información corresponde a cada una**.

Tendréis que analizar las características de los datos y tomar vuestras propias decisiones.

Algunas preguntas que debéis considerar:

- ¿Qué información tiene una estructura estable y claramente definida?
- ¿Qué información necesita establecer relaciones con otros elementos?
- ¿Dónde son necesarias claves foráneas, restricciones de unicidad o transacciones?
- ¿Qué información puede cambiar de estructura con el tiempo?
- ¿Qué consultas realizará la aplicación con mayor frecuencia?
- ¿Qué índices necesita cada sistema?
- ¿Qué sucede cuando se modifica o elimina información relacionada?
- ¿Es conveniente almacenar un dato dentro de otro o mantenerlo separado?
- ¿Qué problemas introduce distribuir información entre diferentes motores?
- ¿Se podría resolver una necesidad con PostgreSQL utilizando `jsonb`? ¿Qué diferencias tendría respecto a MongoDB?

No debéis elegir MongoDB simplemente porque admite documentos JSON, ni PostgreSQL únicamente porque permite crear tablas.

Tendréis que justificar vuestra elección en función de los requisitos.

## Restricciones de integridad obligatorias

Las reglas importantes no deben depender únicamente de la validación de la API.

El almacenamiento deberá proteger, como mínimo:

- Longitudes máximas razonadas para nombres, apellidos, títulos, correos y departamentos.
- Un tipo adecuado para textos largos, como la descripción de una actividad.
- Campos obligatorios y opcionales definidos de forma explícita.
- Unicidad de los correos de estudiantes y docentes.
- Unicidad del nombre de departamento y de etiqueta.
- Unicidad de la pareja actividad–estudiante en las inscripciones.
- Capacidad máxima mayor que cero.
- Fecha de nacimiento anterior a la fecha actual.
- Claves foráneas y comportamiento de borrado razonado.
- Índices alineados con las consultas reales.
- En MongoDB, índices y una validación mínima de campos y tipos sin eliminar la flexibilidad necesaria.

Podéis aplicar estas reglas mediante anotaciones de JPA/Hibernate, scripts SQL, migraciones o configuración de MongoDB. Tendréis que explicar qué mecanismo habéis utilizado y por qué.

Durante la defensa se podrán insertar datos inválidos directamente con `psql` o `mongosh` para comprobar si el almacenamiento los rechaza.

---

# 5. Proyecto inicial proporcionado

El profesor facilitará un proyecto que incluirá:

- Spring Boot 4.1.2 preparado para Java 21.
- Configuración de Maven.
- Controllers REST implementados.
- DTO de entrada y salida.
- Gestión común de errores HTTP.
- Stubs de la capa de servicio.
- Estructura inicial por capas.
- Dependencias para JPA, PostgreSQL, MongoDB, Redis y validación.
- Lombok y MapStruct como herramientas opcionales.
- Annotation processors de Lombok y MapStruct configurados.
- Docker Compose con PostgreSQL, MongoDB y Redis.
- Swagger UI y especificación OpenAPI.
- Spotless para comprobar el formato del código.
- Pruebas públicas de aceptación.
- README y contrato HTTP.

La estructura inicial será:

```
controller/rest      controllers REST y tratamiento de errores HTTP
controller/dto/   contrato de entrada y salida de la API
service/          casos de uso y reglas de negocio
repository/       repositorios Spring Data que debéis implementar
entity/           entidades JPA que debéis implementar
document/         documentos MongoDB que debéis implementar
mapper/           conversión entre persistencia y DTO
```

Los paquetes de persistencia están deliberadamente vacíos.

Cada operación del servicio devuelve inicialmente `501 NOT_IMPLEMENTED`.

## ¿Qué tendréis que implementar?

Como mínimo:

- El diseño del modelo de datos.
- Las entidades JPA y sus relaciones.
- Los documentos de MongoDB necesarios.
- Los repositorios de acceso a datos.
- Las consultas necesarias para guardar y recuperar información.
- La lógica de negocio en la capa `service`.
- Los mecanismos necesarios para mantener la integridad.
- La conversión entre entidades o documentos y DTO.

La conversión puede implementarse manualmente o mediante MapStruct.

Lombok y MapStruct son opcionales: podéis utilizarlos si aportan claridad, pero no son obligatorios.

No debéis modificar los controllers, los DTO, los endpoints o las pruebas para evitar implementar un requisito.

---

# 6. Arranque y documentación de la API

## Requisitos

- JDK 21.
- Maven 3.9 o superior.
- Docker Engine o Docker Desktop con Docker Compose v2.

## Arranque recomendado

Desde la raíz del proyecto:

```bash
mvn spring-boot:run
```

Spring Boot detectará el fichero `compose.yaml` y levantará automáticamente PostgreSQL, MongoDB y Redis antes de iniciar la aplicación.

Al detener la aplicación también se detendrán los contenedores.

No es necesario ejecutar previamente `docker compose up`.

## Gestión manual alternativa

Si preferís administrar los contenedores por separado:

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false
```

Los volúmenes de Docker permiten conservar los datos de desarrollo entre reinicios de los contenedores.

## Swagger y OpenAPI

Con la aplicación arrancada estarán disponibles:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **OpenAPI YAML:** http://localhost:8080/v3/api-docs.yaml

Swagger permite explorar y ejecutar las operaciones durante el desarrollo.

Podéis enriquecer la documentación con anotaciones OpenAPI, pero no cambiar el contrato establecido.

---

# 7. Contrato de la API

El proyecto inicial incluye las siguientes operaciones:

| Operación | Método | Endpoint | Éxito |
| --- | --- | --- | --- |
| Crear departamento | POST | `/api/departamentos` | 201 |
| Registrar docente | POST | `/api/docentes` | 201 |
| Crear grupo | POST | `/api/grupos` | 201 |
| Registrar estudiante | POST | `/api/estudiantes` | 201 |
| Consultar estudiante | GET | `/api/estudiantes/{id}` | 200 |
| Crear actividad | POST | `/api/actividades` | 201 |
| Consultar actividad | GET | `/api/actividades/{id}` | 200 |
| Inscribir estudiante | POST | `/api/actividades/{id}/inscripciones` | 201 |
| Consultar participantes | GET | `/api/actividades/{id}/inscripciones` | 200 |
| Registrar seguimiento | POST | `/api/actividades/{id}/seguimientos` | 201 |
| Consultar detalle completo | GET | `/api/actividades/{id}/detalle` | 200 |
| Consultar historial del estudiante | GET | `/api/estudiantes/{id}/actividades` | 200 |
| Crear o actualizar contacto | PUT | `/api/estudiantes/{id}/contacto` | 200 |
| Consultar contacto | GET | `/api/estudiantes/{id}/contacto` | 200 |
| Crear etiqueta | POST | `/api/etiquetas` | 201 |
| Asociar etiqueta | PUT | `/api/actividades/{id}/etiquetas/{etiquetaId}` | 204 |
| Consultar actividades por etiqueta | GET | `/api/etiquetas/{id}/actividades` | 200 |

Los nombres de los endpoints no determinan qué tablas, documentos o repositorios deben existir.

Los DTO y el fichero `docs/api-contract.md` establecen los campos exactos, la estructura JSON y los códigos HTTP esperados.

## Comportamientos esperados

La aplicación debe responder correctamente ante las operaciones válidas y los errores de negocio.

- `01 Created`: recurso registrado.
- `04 No Content`: relación creada sin cuerpo de respuesta.
- `200 OK`: consulta o actualización realizada.
- `400 Bad Request`: JSON malformado o datos inválidos.
- `404 Not Found`: recurso solicitado o relacionado inexistente.
- `409 Conflict`: duplicado, relación repetida, restricción de integridad o actividad sin plazas.

Los errores no deben exponer sentencias SQL ni detalles internos de la aplicación.

---

# 8. Pruebas automatizadas

El repositorio inicial incorpora pruebas de aceptación con JUnit, Spring Boot Test y Testcontainers.

Las pruebas crean instancias aisladas de PostgreSQL y MongoDB. Durante los tests, el arranque automático de `compose.yaml` está desactivado.

**Inicialmente, las pruebas funcionales fallarán porque la persistencia todavía no está implementada.**

Vuestro objetivo es conseguir que se superen mediante una implementación correcta.

## Comandos

```bash
mvn test
mvn verify
mvn spotless:apply
```

- `mvn test` ejecuta las pruebas unitarias con patrón `Test`.
- `mvn verify` ejecuta las pruebas de integración `IT` y comprueba el formato.
- `mvn spotless:apply` aplica automáticamente el formato Java.

## Comportamientos que se comprobarán

| Prueba | Resultado esperado |
| --- | --- |
| Registrar un estudiante válido | Se almacena y puede recuperarse |
| Registrar un recurso duplicado | Se devuelve un conflicto y no se duplica |
| Crear una actividad con responsable | Se registra correctamente |
| Inscribir un estudiante | Se conserva su inscripción |
| Repetir una inscripción | No se crea una segunda inscripción |
| Completar el aforo | Se impiden nuevas inscripciones |
| Consultar participantes | Se recupera la información correcta |
| Registrar seguimiento variable | Se conserva toda la estructura recibida |
| Consultar el detalle | Se combinan correctamente los datos relacionados |
| Consultar el historial | Se recuperan las actividades del estudiante |
| Guardar una ficha de contacto | Se crea o actualiza sin duplicar la ficha |
| Asociar dos veces una etiqueta | La relación no se duplica |
| Reiniciar la aplicación | La información sigue disponible |

Los tests comprobarán el comportamiento observable, no una solución concreta de tablas o colecciones.

Durante la evaluación se podrán utilizar pruebas privadas con datos diferentes.

| 📌 | **Conseguir que los tests pasen es necesario, pero no suficiente.** También se evaluarán el diseño, la integridad real del almacenamiento y vuestra capacidad para defender la solución. |
| --- | --- | 

---

# 10. Entregables

Cada pareja entregará su trabajo mediante un repositorio Git.

El repositorio debe contener:

## A. Código fuente

La aplicación Spring Boot con las funcionalidades implementadas.

No deben incluirse credenciales, directorios generados ni archivos propios del entorno local.

## B. Modelo de datos

- Diagrama de las estructuras relacionales.
- Explicación de la organización de la información documental.
- Índices y restricciones principales.

## C. Documento de decisiones técnicas

Un documento de una o dos páginas que explique:

- Qué información habéis almacenado en PostgreSQL.
- Qué información habéis almacenado en MongoDB.
- Por qué habéis tomado esas decisiones.
- Qué alternativas habéis considerado.
- Qué riesgos introduce utilizar dos motores.
- Qué limitaciones presenta vuestra solución.
- Qué decisiones cambiaron respecto al diseño inicial.

## D. README

Instrucciones para:

- Cumplir los requisitos previos.
- Arrancar la aplicación.
- Ejecutar las pruebas.
- Consultar directamente PostgreSQL y MongoDB.
- Detener o reiniciar el entorno.

## E. Evidencias de persistencia

Debéis poder demostrar que la información está realmente almacenada.

Durante la defensa se os podrá pedir que localicéis datos concretos utilizando las herramientas de cada motor.

No se admitirán soluciones que simulen la persistencia utilizando únicamente colecciones Java en memoria.

---

# 11. Evaluación

| Criterio | Peso |
| --- | --- |
| Diseño relacional e implementación con JPA/Hibernate | 30 % |
| Modelado documental y persistencia con MongoDB | 20 % |
| Justificación de las decisiones de almacenamiento | 25 % |
| Funcionamiento, integridad y pruebas automatizadas | 15 % |
| Organización, ejecución y defensa técnica individual | 10 % |
| **Total** | **100 %** |
|  |  |

## Qué se valorará especialmente

### Diseño relacional — 30 %

Entidades correctamente identificadas, relaciones coherentes, claves, restricciones e índices adecuados y utilización correcta de JPA e Hibernate.

### Persistencia documental — 20 %

Documentos bien diseñados, conservación exacta de la información, índices, validación mínima y uso apropiado de MongoDB.

### Decisiones arquitectónicas — 25 %

Argumentos técnicos para seleccionar cada sistema, identificación de alternativas y comprensión de los compromisos que introduce cada elección.

### Funcionamiento y pruebas — 15 %

Cumplimiento del contrato, resultados de las pruebas, persistencia real e integridad de la información.

### Organización y defensa — 10 %

Calidad del código, claridad del README, capacidad de localizar los datos, interpretar la implementación y justificar las decisiones.

La actividad se realiza en grupos de hasta 3 personas, pero la defensa individual podrá modificar la calificación de cada integrante.

---

# 12. Ampliación voluntaria: Redis

**Puntuación adicional: hasta 1 punto, sin superar la calificación máxima de 10.**

Redis ya está disponible en `compose.yaml` y la dependencia de Spring Data Redis está incluida en el proyecto.

Su uso es voluntario.

## El problema

La consulta:

```
GET /api/actividades/{id}/detalle
```

puede ejecutarse cientos de veces y necesita combinar información de PostgreSQL y MongoDB.

El centro quiere estudiar si una caché puede evitar accesos repetidos sin comprometer el control de plazas ni mostrar información incorrecta.

## Qué debéis investigar

1. Qué información merece la pena almacenar en caché.
2. Qué clave utilizaríais.
3. Qué tiempo de vida o TTL aplicaríais.
4. Qué operaciones deben invalidar o actualizar la entrada.
5. Qué riesgo existe de mostrar datos obsoletos.
6. Qué debe ocurrir si Redis deja de estar disponible.
7. Por qué el control de plazas no debe depender de la caché.

## Demostración opcional

Si implementáis la ampliación, deberéis demostrar:

- Que una segunda consulta evita accesos repetidos a PostgreSQL y MongoDB.
- Que una nueva inscripción, etiqueta o seguimiento invalida la información afectada.
- Que la API sigue funcionando al detener el contenedor de Redis.

| Criterio | Puntos |
| --- | --- |
| Invalidación o actualización correcta | 0,4 |
| Tolerancia a la caída de Redis | 0,3 |
| Justificación de la caché, TTL y riesgos | 0,3 |
| **Total** | **1,0** |

La ampliación solo se valorará si la actividad principal está completada.