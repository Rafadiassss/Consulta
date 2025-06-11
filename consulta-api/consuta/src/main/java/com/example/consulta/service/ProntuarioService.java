package com.example.consulta.service;

import com.example.consulta.dto.EntradaProntuarioRequestDTO;
import com.example.consulta.dto.ProntuarioRequestDTO;
import com.example.consulta.enums.TipoUsuario;
import com.example.consulta.model.EntradaProntuario;
import com.example.consulta.model.Prontuario;
import com.example.consulta.model.Usuario;
import com.example.consulta.repository.ProntuarioRepository;
import com.example.consulta.repository.UsuarioRepository;
import com.example.consulta.vo.EntradaProntuarioVO;
import com.example.consulta.vo.ProntuarioVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProntuarioService {

    @Autowired
    private ProntuarioRepository prontuarioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public ProntuarioVO criarProntuario(Long idUsuario, ProntuarioRequestDTO dto) {
        // Busca o usuário. Se não encontrar, lança uma exceção padrão.
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário (médico) não encontrado."));

        // Valida a permissão. Se não for médico, lança IllegalArgumentException.
        if (usuario.getTipo() != TipoUsuario.medico) {
            throw new IllegalArgumentException("Apenas médicos podem criar prontuários.");
        }

        Prontuario prontuario = new Prontuario();
        prontuario.setNumero(dto.numero());
        Prontuario prontuarioSalvo = prontuarioRepository.save(prontuario);

        return toVO(prontuarioSalvo);
    }

    public ProntuarioVO buscarProntuario(Long idUsuario, Long idProntuario) {
        // Valida o usuário.
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        if (usuario.getTipo() != TipoUsuario.medico) {
            throw new IllegalArgumentException("Apenas médicos podem visualizar prontuários.");
        }

        // Busca o prontuário.
        Prontuario prontuario = prontuarioRepository.findById(idProntuario)
                .orElseThrow(() -> new RuntimeException("Prontuário não encontrado."));

        return toVO(prontuario);
    }

    public ProntuarioVO adicionarEntrada(Long idProntuario, EntradaProntuarioRequestDTO dto) {
        Prontuario prontuario = prontuarioRepository.findById(idProntuario)
                .orElseThrow(() -> new RuntimeException("Prontuário principal não encontrado."));

        EntradaProntuario novaEntrada = toEntradaEntity(dto);
        novaEntrada.setDataEntrada(LocalDateTime.now());

        prontuario.adicionarEntrada(novaEntrada);

        Prontuario prontuarioAtualizado = prontuarioRepository.save(prontuario);

        return toVO(prontuarioAtualizado);
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    private ProntuarioVO toVO(Prontuario prontuario) {
        List<EntradaProntuarioVO> entradasVO = prontuario.getEntradas().stream()
                .map(this::toEntradaVO).collect(Collectors.toList());
        return new ProntuarioVO(prontuario.getId(), prontuario.getNumero(), entradasVO);
    }

    private EntradaProntuarioVO toEntradaVO(EntradaProntuario entrada) {
        return new EntradaProntuarioVO(entrada.getId(), entrada.getDataEntrada(), entrada.getDiagnostico(),
                entrada.getTratamento(), entrada.getObservacoes());
    }

    private EntradaProntuario toEntradaEntity(EntradaProntuarioRequestDTO dto) {
        EntradaProntuario entrada = new EntradaProntuario();
        entrada.setDiagnostico(dto.diagnostico());
        entrada.setTratamento(dto.tratamento());
        entrada.setObservacoes(dto.observacoes());
        return entrada;
    }

    @Cacheable("prontuarios") // Ainda é uma boa ideia manter o cache aqui
    public List<ProntuarioVO> listarTodosSemValidacao() {
        System.out.println("Buscando TODOS os prontuários no banco (requisição de desenvolvimento)...");
        return prontuarioRepository.findAll()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }
}