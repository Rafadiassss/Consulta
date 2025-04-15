package consulta.com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import consulta.com.example.demo.model.Usuario;
import consulta.com.example.demo.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuario", description = "Operações para gerenciar usuários")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Usuario salvar(@RequestBody Usuario usuario) {
        return usuarioService.salvar(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        return usuarioService.buscarPorId(id).map(usuario -> {
            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setUsername(usuarioAtualizado.getUsername());
            usuario.setSenha(usuarioAtualizado.getSenha());
            usuario.setTipo(usuarioAtualizado.getTipo());
            usuario.setCpf(usuarioAtualizado.getCpf());
            usuario.setCrm(usuarioAtualizado.getCrm());
            usuario.setEspecialidade(usuarioAtualizado.getEspecialidade());
            return ResponseEntity.ok(usuarioService.salvar(usuario));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return usuarioService.buscarPorId(id).map(usuario -> {
            usuarioService.deletar(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
