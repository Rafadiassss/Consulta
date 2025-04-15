package consulta.com.example.demo.service;

import consulta.com.example.demo.model.Secretaria;
import consulta.com.example.demo.repository.SecretariaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SecretariaService {

    @Autowired
    private SecretariaRepository repository;

    public List<Secretaria> listarTodas() {
        return repository.findAll();
    }

    public Secretaria salvar(Secretaria secretaria) {
        return repository.save(secretaria);
    }

    public Optional<Secretaria> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Secretaria atualizar(Long id, Secretaria secretaria) {
        secretaria.setId(id);
        return repository.save(secretaria);
    }
}
