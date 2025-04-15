package consulta.com.example.demo.controller;

import consulta.com.example.demo.model.Secretaria;
import consulta.com.example.demo.service.SecretariaService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/secretarias")
@Tag(name = "Secretaria", description = "Operações relacionadas às secretárias")
public class SecretariaController {

    @Autowired
    private SecretariaService service;

    @GetMapping
    public List<Secretaria> listarTodas() {
        return service.listarTodas();
    }

    @PostMapping
    public Secretaria cadastrar(@RequestBody Secretaria secretaria) {
        return service.salvar(secretaria);
    }

    @GetMapping("/{id}")
    public Secretaria buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Secretaria atualizar(@PathVariable Long id, @RequestBody Secretaria secretaria) {
        return service.atualizar(id, secretaria);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
