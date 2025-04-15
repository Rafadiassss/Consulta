package consulta.com.example.demo.repository;
/*
import consulta.com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CadastroService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private SecretariaRepository secretariaRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    public void cadastrarTudo() {
        // Cadastra Paciente
        Paciente paciente = new Paciente();
        paciente.setNome("João da Silva");
        paciente.setCpf("123.456.789-00");
        pacienteRepository.save(paciente);

        // Cadastra Secretaria
        Secretaria secretaria = new Secretaria();
        secretaria.setNome("Maria Oliveira");
        secretaria.setCpf("987.654.321-00");
        secretariaRepository.save(secretaria);

        // Cadastra Consulta
        Consulta consulta = new Consulta();
        consulta.setData("2025-04-10");
        consulta.setNomeDentista("Dr. Pedro Dentista");
        consulta.setPaciente(paciente);
        consulta.setSecretaria(secretaria);
        consultaRepository.save(consulta);

        // Cadastra Agenda
        Agenda agenda = new Agenda();
        agenda.setData("2025-04-10");
        agenda.setConsulta(consulta);
        agendaRepository.save(agenda);

        System.out.println("Todos os dados foram cadastrados com sucesso!");
    }
}
Você pode chamar o método cadastrarTudo() em algum ponto do seu código, como em um controlador ou em um método de inicialização, para realizar o cadastro de todos os dados de uma vez. Certifique-se de que as classes Paciente, Secretaria, Consulta e Agenda estejam corretamente definidas e mapeadas para suas respectivas tabelas no banco de dados.
*/