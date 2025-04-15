package consulta.com.example.demo.service;

import consulta.com.example.demo.model.Paciente;
import consulta.com.example.demo.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    public List<Paciente> listarTodos() {
        return repository.findAll();
    }

    public Paciente salvar(Paciente paciente) {
        return repository.save(paciente);
    }

    public Optional<Paciente> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Paciente atualizar(Long id, Paciente paciente) {
        paciente.setId(id);
        return repository.save(paciente);
    }
}
