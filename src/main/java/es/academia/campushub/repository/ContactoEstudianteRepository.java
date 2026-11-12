package es.academia.campushub.repository;

import es.academia.campushub.entity.ContactoEstudiante;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactoEstudianteRepository extends JpaRepository<ContactoEstudiante, Long> {
  Optional<ContactoEstudiante> findByEstudianteId(Long estudianteId);
}
