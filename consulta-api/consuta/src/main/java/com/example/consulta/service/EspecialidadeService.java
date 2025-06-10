package com.example.consulta.service;

import com.example.consulta.model.Especialidade;
import com.example.consulta.repository.EspecialidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadeService {
 
    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    // A primeira vez que esse metodo for chamado ele executa e guarda a lista no
    // cache, e nas proximas chamadas do metodo, ele retorna a lista diretamente do
    // cache
    @Cacheable("especialidades")
    public List<Especialidade> listarTodas() {
        // Log para depuração do cache
        System.out.println("Buscando especialidades no banco...");
        return especialidadeRepository.findAll();
    }

    // Basicamente aqui cada especialidade com seu id vai ser armazenada em cache
    // qunando esse metodo for chamado. O "key" é onde vai ser armazenda o ID da
    // especialidade em cache
    @Cacheable(value = "especialidades", key = "#id")
    public Optional<Especialidade> buscarPorId(Long id) {
        // Log para depuração do cache
        System.out.println("Buscando especialidade de ID " + id + " no banco...");
        return especialidadeRepository.findById(id);
    }

    // Quando salvar uma nova especialidade, o cache onde guarda a lista completa de
    // todas as especialidades precisa ser limpo e incluir essa nova especialidades.
    // O 'allEntries=ture' faz essa limpeza do cache 'especialidades'
    @CacheEvict(value = "especialidades", allEntries = true)
    // Adiciona/atualiza a especialidade no cache "especialidade"
    @CachePut(value = "especialidade", key = "#especialidade.id")
    public Especialidade salvar(Especialidade especialidade) {
        return especialidadeRepository.save(especialidade);
    }

    // Quando o delete for executado, é limpado a lista completa de 'especialidades'
    // quanto a entrada especifica do chache 'especialidade' que corresponde ao ID
    // deletado
    @CacheEvict(value = { "especialidades", "especialidade" }, key = "#id", allEntries = true)
    public boolean deletar(Long id) {
        if (especialidadeRepository.existsById(id)) {
            especialidadeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Ao atualizar também é limpado a lista completa para refletir a mudança.
    // E também é limpado o cache específico da especialidade que foi alterada.
    @CacheEvict(value = { "especialidades", "especialidade" }, allEntries = true)
    public Optional<Especialidade> atualizar(Long id, Especialidade dadosNovos) {
        return especialidadeRepository.findById(id)
                .map(especialidadeExistente -> {
                    especialidadeExistente.setNome(dadosNovos.getNome());
                    // Adicione outros campos para atualizar se houver
                    return especialidadeRepository.save(especialidadeExistente);
                });
    }
}
