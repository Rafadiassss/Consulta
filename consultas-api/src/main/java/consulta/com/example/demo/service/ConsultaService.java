package consulta.com.example.demo.service;

import consulta.com.example.demo.model.Consulta;
import consulta.com.example.demo.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository repository;

    public List<Consulta> listarTodas() {
        return repository.findAll();
    }

    public Optional<Consulta> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<Consulta> listarPorPaciente(Long pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }

    public List<Consulta> listarPorSecretaria(Long secretariaId) {
        return repository.findBySecretariaId(secretariaId);
    }

    public List<Consulta> listarPorMedico(Long medicoId) {
        return repository.findByMedicoId(medicoId);
    }

    public Consulta salvar(Consulta consulta) {
        return repository.save(consulta);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Optional<Consulta> atualizar(Long id, Consulta novaConsulta) {
        return repository.findById(id).map(consulta -> {
            consulta.setData(novaConsulta.getData());
            consulta.setMedico(novaConsulta.getMedico());
            consulta.setPaciente(novaConsulta.getPaciente());
            consulta.setSecretaria(novaConsulta.getSecretaria());
            return repository.save(consulta);
        });
    }
}
