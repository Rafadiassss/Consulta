package consulta.com.example.demo.model;


import jakarta.persistence.*;

@Entity
@Table(name = "agenda")
public
class Agenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String data;

    @OneToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Consulta getConsulta() {
        return consulta;
    }
    
    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    
}