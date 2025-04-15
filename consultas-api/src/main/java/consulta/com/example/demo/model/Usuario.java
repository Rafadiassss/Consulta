package consulta.com.example.demo.model;

import jakarta.persistence.*;


@Entity
public class Usuario {
    public enum TipoUsuario {
        PACIENTE,
        MEDICO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String username; // pode ser o e-mail ou outro identificador
    private String senha;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipo; // Enum para "PACIENTE" ou "MEDICO"

    private String cpf; // CPF do paciente, se for um paciente
    private String crm; // CRM do médico, se for um médico
    private String especialidade; // Especialidade do médico, se for um médico

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public TipoUsuario getTipo() {
        return tipo;
    }
    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getCrm() {
        return crm;
    }
    public void setCrm(String crm) {
        this.crm = crm;
    }
    public String getEspecialidade() {
        return especialidade;
    }
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
