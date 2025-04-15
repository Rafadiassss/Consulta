package consulta.com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "consulta")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Usuario medico;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "secretaria_id", nullable = false)
    private Secretaria secretaria;

    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL)
    private Agenda agenda;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public Usuario getMedico() { return medico; }
    public void setMedico(Usuario medico) { this.medico = medico; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Secretaria getSecretaria() { return secretaria; }
    public void setSecretaria(Secretaria secretaria) { this.secretaria = secretaria; }

    public Agenda getAgenda() { return agenda; }
    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
        if (agenda != null) {
            agenda.setConsulta(this);
        }
    }
}
