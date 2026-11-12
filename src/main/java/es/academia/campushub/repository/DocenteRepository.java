package es.academia.campushub.repository;

import es.academia.campushub.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocenteRepository extends JpaRepository<Docente, Long> {
  boolean existsByEmailIgnoreCase(String email);
}
