package consulta.com.example.demo.controller;

import consulta.com.example.demo.model.Consulta;
import consulta.com.example.demo.service.ConsultaService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
@CrossOrigin(origins = "*")
@Tag(name = "Consulta", description = "Operações para gerenciar consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService service;

    @GetMapping
    public List<Consulta> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consulta> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<Consulta> listarPorPaciente(@PathVariable Long pacienteId) {
        return service.listarPorPaciente(pacienteId);
    }

    @GetMapping("/secretaria/{secretariaId}")
    public List<Consulta> listarPorSecretaria(@PathVariable Long secretariaId) {
        return service.listarPorSecretaria(secretariaId);
    }

    @GetMapping("/medico/{medicoId}")
    public List<Consulta> listarPorMedico(@PathVariable Long medicoId) {
        return service.listarPorMedico(medicoId);
    }

    @PostMapping
    public ResponseEntity<Consulta> cadastrar(@RequestBody Consulta consulta) {
        Consulta salva = service.salvar(consulta);
        return ResponseEntity.status(201).body(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consulta> atualizar(
            @PathVariable Long id,
            @RequestBody Consulta consultaAtualizada) {

        return service.atualizar(id, consultaAtualizada)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (service.buscarPorId(id).isPresent()) {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
