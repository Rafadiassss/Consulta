package consulta.com.example.demo.repository;

import consulta.com.example.demo.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByPacienteId(Long pacienteId);
    List<Consulta> findBySecretariaId(Long secretariaId);
    List<Consulta> findByMedicoId(Long medicoId);
}
