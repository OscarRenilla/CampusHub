// Paquete de los DTO que viajan por la API REST.
package es.academia.campushub.controller.dto;

// Mapa clave-valor para los datos de estructura libre del seguimiento.
import java.util.Map;

/**
 * Cuerpo JSON recibido en POST /api/actividades/{id}/seguimientos.
 * <p>
 * El campo datos es flexible (mapas, listas, textos...) porque se guarda en MongoDB.
 */
public record CreateSeguimientoRequest(
    // Tipo de seguimiento (obligatorio, no vacío).
    String tipo,
    // Datos libres del seguimiento (obligatorio, puede contener estructuras anidadas).
    Map<String, Object> datos) {}
