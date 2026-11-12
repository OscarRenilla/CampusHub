package es.academia.campushub.service;

import es.academia.campushub.controller.dto.CreateSeguimientoRequest;
import es.academia.campushub.controller.dto.SeguimientoResponse;
import java.util.List;

public interface SeguimientoServiceImpl {

    SeguimientoResponse registrarSeguimiento(
            Long actividadId,
            CreateSeguimientoRequest request
    );

    List<SeguimientoResponse> obtenerSeguimientos(
            Long actividadId
    );
}