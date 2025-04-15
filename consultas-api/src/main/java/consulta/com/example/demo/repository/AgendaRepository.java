package consulta.com.example.demo.repository;

import consulta.com.example.demo.model.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
