package consulta.com.example.demo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "secretaria")
public class Secretaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Se quiser manter o RG como campo específico:
    private String rg;

    // Referência única ao Usuario (onde está nome, username, senha, cpf…)
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "secretaria")
    private List<Consulta> consultas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Consulta> getConsultas() { return consultas; }
    public void setConsultas(List<Consulta> consultas) { this.consultas = consultas; }
}
