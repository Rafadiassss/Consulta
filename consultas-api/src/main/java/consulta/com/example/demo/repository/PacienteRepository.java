package consulta.com.example.demo.repository;

import consulta.com.example.demo.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}
