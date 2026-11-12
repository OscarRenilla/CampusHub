# Arquitectura de persistencia de CampusHub

## 1. Objetivo

CampusHub utiliza distintas tecnologías de persistencia según la naturaleza de la información que gestiona. PostgreSQL es la base de datos principal para los datos académicos y sus relaciones; MongoDB almacena los seguimientos de las actividades, cuyo contenido puede variar; Redis queda disponible como posible apoyo de infraestructura, pero no se incorpora a la lógica de negocio mientras no exista una necesidad concreta.

## 2. PostgreSQL: información académica y relacional

PostgreSQL es adecuado para los datos estructurados del dominio:

- Departamentos.
- Docentes.
- Grupos.
- Estudiantes.
- Contactos de estudiantes.
- Actividades.
- Inscripciones.
- Etiquetas y sus asociaciones con actividades.

Estos datos tienen propiedades conocidas y relaciones que deben mantenerse coherentes. Por ejemplo, un docente pertenece a un departamento, un estudiante pertenece a un grupo y una inscripción relaciona a un estudiante con una actividad.

El modelo relacional permite expresar estas relaciones mediante claves primarias y claves foráneas. Las claves foráneas impiden que existan referencias a registros inexistentes y ayudan a mantener la integridad entre las tablas.

También son importantes las restricciones:

- `NOT NULL` obliga a proporcionar los datos requeridos.
- `UNIQUE` evita duplicados en campos que deben identificar de forma única un dato, como los correos electrónicos o el nombre de una etiqueta.
- `CHECK` permite imponer condiciones de dominio directamente en la base de datos, como exigir que `max_participantes` sea mayor que cero.
- Las restricciones únicas compuestas permiten impedir que un estudiante se inscriba más de una vez en la misma actividad.

Estas reglas deben estar respaldadas por la base de datos, además de las validaciones realizadas desde Java. Así la integridad no depende exclusivamente de que todas las peticiones pasen por una determinada ruta de la aplicación.

## 3. MongoDB: seguimientos de actividades

MongoDB se utiliza para almacenar los seguimientos asociados a las actividades. Un seguimiento contiene:

- Un identificador propio.
- `actividadId`, que identifica la actividad relacionada.
- `tipo`, que clasifica el seguimiento.
- `datos`, un mapa de estructura flexible.
- `creadoEn`, la fecha y hora de creación.

El campo `datos` se representa en Java mediante `Map<String, Object>`. De esta forma puede contener cadenas, números, booleanos, listas y mapas anidados. La información se conserva como estructura BSON/JSON y no se serializa manualmente como una cadena de texto.

Esta flexibilidad resulta útil porque distintos tipos de seguimiento pueden necesitar propiedades diferentes. Añadir un nuevo tipo de seguimiento no obliga necesariamente a modificar el esquema relacional ni a crear una tabla distinta para cada variante.

### Referencia a la actividad

El documento almacena `actividadId` como referencia lógica a la actividad de PostgreSQL. No se duplica el objeto `Actividad` en MongoDB.

Esta decisión evita mantener dos copias de los mismos datos, que podrían quedar desactualizadas si la actividad cambia. PostgreSQL sigue siendo la fuente de verdad de la actividad y MongoDB almacena únicamente la información propia del seguimiento.

La aplicación debe comprobar que la actividad existe en PostgreSQL antes de registrar un seguimiento. La asociación entre ambas bases de datos se realiza mediante el identificador, no mediante una relación JPA ni una transacción distribuida.

### Consulta del detalle

Para construir el detalle de una actividad, la aplicación obtiene de PostgreSQL la actividad, el docente responsable, las inscripciones, los estudiantes y las etiquetas. Después consulta MongoDB por `actividadId` y añade los seguimientos al DTO de respuesta.

Las dos consultas se coordinan desde la capa de servicio. La ausencia de seguimientos debe representarse mediante una lista vacía, igual que la ausencia de estudiantes o etiquetas.

## 4. Redis: valoración de uso

Redis está configurado como tecnología disponible en el entorno, pero su presencia en la configuración o en Docker no implica que deba utilizarse obligatoriamente.

Podría aportar valor en el futuro para:

- Almacenar en caché consultas de lectura muy frecuentes.
- Gestionar sesiones compartidas si la aplicación se despliega con varias instancias.
- Mantener contadores temporales o mecanismos auxiliares de coordinación.

Sin embargo, para el alcance actual de CampusHub no se ha identificado una necesidad que justifique introducir Redis en los casos de uso. La caché requeriría definir expiraciones e invalidaciones; las sesiones dependerían de una estrategia de autenticación y despliegue; y los contadores de aforo deben resolverse primero con garantías transaccionales en PostgreSQL.

Por tanto, Redis queda configurado y disponible, pero no participa actualmente en la lógica de negocio. Se reconsiderará cuando exista un requisito medible que lo justifique.

## 5. Resumen de responsabilidades

| Tecnología | Responsabilidad |
|---|---|
| PostgreSQL | Fuente principal de los datos académicos estructurados y sus relaciones. |
| MongoDB | Almacenamiento de seguimientos con datos variables asociados mediante `actividadId`. |
| Redis | Componente opcional, reservado para necesidades futuras de caché, sesiones o coordinación. |

Esta separación mantiene cada tipo de información en el sistema más adecuado y evita duplicar datos o añadir complejidad tecnológica sin una necesidad funcional.
