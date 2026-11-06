# Guía práctica: MongoDB con Java y Spring Data MongoDB

## 1. De JSON a un documento Java

Imaginemos que tenemos una colección de videojuegos en MongoDB:

```json
db.videojuegos.insertOne({
  titulo: "Hollow Knight",
  estudio: "Team Cherry",
  precio: 14.99,
  lanzamiento: 2017,
  generos: ["Metroidvania", "Acción"],
  disponible: true
})
```

MongoDB almacena documentos BSON que podemos representar de forma similar a JSON.

En Java, Spring Data MongoDB nos permite mapear estos documentos a objetos.

```java
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "videojuegos")
public record Videojuego(
    @Id String id,
    String titulo,
    String estudio,
    BigDecimal precio,
    int lanzamiento,
    List<String> generos,
    boolean disponible
) {}
```

Las anotaciones importantes son:

| Anotación | Función |
| --- | --- |
| `@Document` | Indica la colección de MongoDB |
| `@Id` | Identifica el campo `_id` |
| `@Field` | Permite establecer otro nombre para un campo |
| `@Indexed` | Permite declarar un índice |
| `@Transient` | Excluye un atributo de la persistencia |

Por ejemplo:

```java
@Field("nombre_comercial")
String titulo
```

Esto permite que el atributo Java `titulo` se almacene en MongoDB como `nombre_comercial`.

## Documentos con estructuras anidadas

Una de las posibilidades más interesantes de MongoDB es almacenar objetos y arrays dentro de otros documentos.

Supongamos que un videojuego tiene información sobre su estudio:

```json
{
  "titulo": "Hollow Knight",
  "estudio": {
    "nombre": "Team Cherry",
    "pais": "Australia",
    "fundacion": 2014
  },
  "plataformas": ["PC", "Switch", "PS4"]
}
```

Podemos definir un record para el objeto anidado:

```java
public record Estudio(
    String nombre,
    String pais,
    int fundacion
) {}
```

Y utilizarlo directamente dentro de otro documento:

```java
@Document(collection = "videojuegos")
public record Videojuego(
    @Id String id,
    String titulo,
    Estudio estudio,
    List<String> plataformas
) {}
```

No necesitamos `@OneToOne`, `@ManyToOne` ni otra anotación de relación JPA.

Un documento puede contener subdocumentos y arrays. No es necesario crear una colección para cada objeto.

## 3. Crear el repositorio

Spring Data MongoDB proporciona la interfaz `MongoRepository`:

```java
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideojuegoRepository
        extends MongoRepository<Videojuego, String> {
}
```

Los dos parámetros genéricos son:

- `Videojuego`: tipo de documento.
- `String`: tipo del identificador.

Sin implementar nada, obtenemos:

```java
repository.save(videojuego);
repository.findAll();
repository.findById(id);
repository.existsById(id);
repository.count();
repository.deleteById(id);
```

Spring Data se encarga de construir y ejecutar las operaciones correspondientes en MongoDB.

## 4. Definir consultas mediante nombres de métodos

Esta es una de las características más prácticas de Spring Data.

Podemos declarar métodos de consulta sin escribir su implementación ni la consulta MongoDB.

```java
public interface VideojuegoRepository
        extends MongoRepository<Videojuego, String> {

    List<Videojuego> findByTitulo(String titulo);

    List<Videojuego> findByLanzamiento(int lanzamiento);

    List<Videojuego> findByDisponibleTrue();

    List<Videojuego> findByPrecioLessThan(BigDecimal precio);

    List<Videojuego> findByLanzamientoGreaterThan(int anio);

    List<Videojuego> findByTituloContainingIgnoreCase(String texto);
}
```

Spring Data interpreta los nombres de los métodos y genera las consultas automáticamente.

### Operadores más habituales

| Operador Spring Data | Ejemplo |
| --- | --- |
| `findBy` | `findByTitulo` |
| `And` | `findByEstudioAndDisponible` |
| `Or` | `findByTituloOrEstudio` |
| `LessThan` | `findByPrecioLessThan` |
| `GreaterThan` | `findByLanzamientoGreaterThan` |
| `Between` | `findByPrecioBetween` |
| `In` | `findByTituloIn` |
| `Containing` | `findByTituloContaining` |
| `IgnoreCase` | `findByTituloIgnoreCase` |
| `OrderBy...Asc` | `findByDisponibleTrueOrderByTituloAsc` |

Por ejemplo:

```java
List<Videojuego> findByDisponibleTrueAndLanzamientoGreaterThan(
    int lanzamiento
);
```

Recuperaría videojuegos disponibles lanzados después de un determinado año.

Importante: los nombres de los métodos se basan en las propiedades del objeto Java, no directamente en los nombres BSON almacenados.

## 5. Consultas explícitas con `@Query`

Cuando una consulta es más compleja, podemos escribirla mediante la anotación `@Query`.

En lugar de depender exclusivamente del nombre del método, escribimos la expresión MongoDB.

```java
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.mongodb.repository.Query;

public interface VideojuegoRepository
        extends MongoRepository<Videojuego, String> {

    @Query("{ 'disponible': true }")
    List<Videojuego> buscarDisponibles();

    @Query("{ 'lanzamiento': { '$gte': ?0 } }")
    List<Videojuego> buscarDesde(int anio);

    @Query("{ 'precio': { '$gte': ?0, '$lte': ?1 } }")
    List<Videojuego> buscarPorPrecio(
        BigDecimal minimo,
        BigDecimal maximo
    );

    @Query("{ 'titulo': { '$regex': ?0, '$options': 'i' } }")
    List<Videojuego> buscarPorTitulo(String expresion);
}
```

Los parámetros se identifican por su posición:

- `?0`: primer argumento.
- `?1`: segundo argumento.
- `?2`: tercer argumento.

### Equivalencia con consultas MongoDB

| Spring Data | MongoDB |
| --- | --- |
| `findByDisponibleTrue()` | `{ disponible: true }` |
| `findByPrecioLessThan(20)` | `{ precio: { $lt: 20 } }` |
| `findByLanzamientoGreaterThan(2020)` | `{ lanzamiento: { $gt: 2020 } }` |
| `findByTituloContaining("Knight")` | Consulta de coincidencia parcial |
| `findByEstudioPais("Australia")` | `{ "estudio.pais": "Australia" }` |

En el último ejemplo, `estudio` es un objeto anidado y Spring Data permite acceder a sus propiedades. También podríamos declarar `findByEstudio_Pais(String pais)` para hacer explícito el recorrido.

## 6. Consultas sobre arrays y documentos anidados

Partimos del modelo con `Estudio estudio` y `List<String> plataformas`.

### Buscar videojuegos de una plataforma

```java
List<Videojuego> findByPlataformasContaining(String plataforma);
```

Por ejemplo:

```java
repository.findByPlataformasContaining("Switch");
```

MongoDB buscaría los documentos cuyo array `plataformas` contiene ese valor.

### Buscar por una propiedad anidada

```java
List<Videojuego> findByEstudio_Pais(String pais);
```

Equivale a:

```jsx
db.videojuegos.find({
  "estudio.pais": "Australia"
})
```

### Buscar documentos con varias condiciones

```java
@Query("""
    {
      'estudio.pais': ?0,
      'plataformas': ?1
    }
    """)
List<Videojuego> buscarPorPaisYPlataforma(
    String pais,
    String plataforma
);
```

Para utilizar esta consulta:

```java
repository.buscarPorPaisYPlataforma(
    "Australia",
    "Switch"
);
```

## 7. Ordenación y paginación

También podemos ordenar sin escribir consultas MongoDB.

```java
List<Videojuego> findByDisponibleTrueOrderByTituloAsc();
```

O utilizar `Sort`:

```java
import org.springframework.data.domain.Sort;

List<Videojuego> videojuegos = repository.findAll(
    Sort.by(Sort.Direction.DESC, "lanzamiento")
);
```

Para paginación:

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

Page<Videojuego> findByDisponibleTrue(Pageable pageable);
```

Y al ejecutarlo:

```java
import org.springframework.data.domain.PageRequest;

Page<Videojuego> pagina = repository.findByDisponibleTrue(
    PageRequest.of(0, 10)
);
```

Devuelve la primera página, con un máximo de diez documentos.

## 8. Consultas dinámicas con MongoTemplate

`MongoRepository` es apropiado para operaciones habituales y consultas conocidas. Cuando necesitamos construir consultas de forma dinámica, podemos utilizar `MongoTemplate`.

Por ejemplo:

```java
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

Query query = new Query();

query.addCriteria(
    Criteria.where("lanzamiento").gte(2020)
);

query.addCriteria(
    Criteria.where("disponible").is(true)
);

List<Videojuego> resultados =
    mongoTemplate.find(query, Videojuego.class);
```

Esta consulta equivale aproximadamente a:

```jsx
db.videojuegos.find({
  lanzamiento: { $gte: 2020 },
  disponible: true
})
```

Las condiciones pueden añadirse dependiendo de los filtros que establezca el usuario. También podemos utilizar `regex`, `in`, `exists`, `orOperator` y muchos otros operadores.

[](https://www.google.com/s2/favicons?domain=https://docs.spring.io&sz=32)

Home

## 9. Índices

Finalmente, conviene que comprendan que las consultas no tienen todas el mismo coste.

Podemos declarar un índice sobre un campo:

```java
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "videojuegos")
public record Videojuego(
    @Id String id,

    @Indexed
    String titulo,

    int lanzamiento
) {}
```

Esto declara el índice, pero su creación automática depende de la configuración de Spring Data MongoDB. Podemos habilitarla para la práctica con:

```
spring.data.mongodb.auto-index-creation=true
```

En MongoDB también podemos crear el índice manualmente:

```
db.videojuegos.createIndex({ titulo: 1 })
```

Y examinar cómo se ejecuta una consulta:

```
db.videojuegos.find({
  titulo: "Hollow Knight"
}).explain("executionStats")
```

El índice puede mejorar significativamente determinadas búsquedas, aunque también ocupa espacio y tiene un coste al insertar o actualizar documentos.

--- 
Documentación oficial: Spring Data MongoDB — Query Methods y MongoTemplate — Query Operations.