package consulta.com.example.demo.service;

import consulta.com.example.demo.model.Agenda;
import consulta.com.example.demo.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository repository;

    public List<Agenda> listarTodas() {
        return repository.findAll();
    }

    public Agenda salvar(Agenda agenda) {
        return repository.save(agenda);
    }

    public Optional<Agenda> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Agenda atualizar(Long id, Agenda agenda) {
        agenda.setId(id);
        return repository.save(agenda);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
