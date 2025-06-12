-- Script de criação para PostgreSQL

CREATE TABLE especialidade (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT
);

CREATE TABLE procedimento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    valor DOUBLE PRECISION NOT NULL
);

CREATE TABLE secretaria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(255) UNIQUE,
    telefone VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    usuario VARCHAR(255) UNIQUE,
    senha VARCHAR(255)
);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    dtype VARCHAR(31) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255),
    tipo VARCHAR(255),
    telefone VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    data_nascimento DATE,
    crm VARCHAR(255),
    especialidade_id BIGINT,
    cpf VARCHAR(255) UNIQUE,
    cartao_sus VARCHAR(255),
    FOREIGN KEY (especialidade_id) REFERENCES especialidade(id)
);

CREATE TABLE prontuario (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(255) UNIQUE
);

CREATE TABLE entrada_prontuario (
    id BIGSERIAL PRIMARY KEY,
    data_entrada TIMESTAMP NOT NULL,
    diagnostico TEXT,
    tratamento TEXT,
    observacoes TEXT,
    prontuario_id BIGINT,
    FOREIGN KEY (prontuario_id) REFERENCES prontuario(id)
);

CREATE TABLE consulta (
    id BIGSERIAL PRIMARY KEY,
    data TIMESTAMP,
    status VARCHAR(255),
    nome_consulta VARCHAR(255),
    paciente_id BIGINT,
    medico_id BIGINT,
    pagamento_id BIGINT UNIQUE,
    prontuario_id BIGINT UNIQUE,
    FOREIGN KEY (paciente_id) REFERENCES usuarios(id),
    FOREIGN KEY (medico_id) REFERENCES usuarios(id),
    FOREIGN KEY (prontuario_id) REFERENCES prontuario(id)
);

CREATE TABLE pagamento (
    id BIGSERIAL PRIMARY KEY,
    data_pagamento DATE,
    valor_pago DECIMAL(19,2),
    forma_pagamento VARCHAR(255),
    status VARCHAR(255),
    consulta_id BIGINT,
    FOREIGN KEY (consulta_id) REFERENCES consulta(id)
);

ALTER TABLE consulta ADD CONSTRAINT fk_pagamento FOREIGN KEY (pagamento_id) REFERENCES pagamento(id);

CREATE TABLE exame (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255),
    resultado TEXT,
    observacoes TEXT,
    consulta_id BIGINT,
    FOREIGN KEY (consulta_id) REFERENCES consulta(id)
);

CREATE TABLE agenda (
    id BIGSERIAL PRIMARY KEY,
    data_agendada DATE
);

CREATE TABLE agenda_horarios (
    agenda_id BIGINT NOT NULL,
    horarios TIME,
    FOREIGN KEY (agenda_id) REFERENCES agenda(id)
);