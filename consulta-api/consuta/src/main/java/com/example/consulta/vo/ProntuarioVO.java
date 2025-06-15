package com.example.consulta.vo;

import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Pagamento;
import com.example.consulta.model.Consulta;
import java.time.LocalDateTime;

// VO da Consulta. Note que ele carrega os objetos de modelo completos,
// pois o serviço pode precisar da lógica de negócio contida neles.
public record ProntuarioVO(
        Long id,
        LocalDateTime data,
        String status,
        String nomeConsulta,
        Paciente paciente,
        Medico medico,
        Pagamento pagamento,
        Consulta prontuario) {
}