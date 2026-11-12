package es.academia.campushub.repository;

import es.academia.campushub.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
  boolean existsByNombreIgnoreCase(String nombre);
}
