package es.academia.campushub.repository;

import es.academia.campushub.document.Seguimiento;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SeguimientoRepository
        extends MongoRepository<Seguimiento, String> {

    List<Seguimiento> findByActividadId(Long actividadId);

    List<Seguimiento> findByActividadIdOrderByCreadoEnDesc(
            Long actividadId
    );
}