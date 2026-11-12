package es.academia.campushub.repository;

import es.academia.campushub.entity.Etiqueta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
