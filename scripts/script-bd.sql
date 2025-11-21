-- Script de criação do banco MindCare
-- Inclui tabelas principais e índices

-- Tabela de Usuários
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    cargo VARCHAR(100),
    receber_notificacoes BOOLEAN DEFAULT TRUE,
    preferencia_idioma VARCHAR(10) DEFAULT 'pt-BR',
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP
);
CREATE INDEX idx_usuario_email ON usuarios(email);
CREATE INDEX idx_usuario_ativo ON usuarios(ativo);

-- Tabela de Registros de Humor
CREATE TABLE registros_humor (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nivel_humor INTEGER NOT NULL CHECK (nivel_humor BETWEEN 1 AND 5),
    emocao VARCHAR(50) NOT NULL,
    descricao VARCHAR(500),
    data DATE NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_humor_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) ON DELETE CASCADE
);
CREATE INDEX idx_humor_usuario_data ON registros_humor(usuario_id, data DESC);
CREATE INDEX idx_humor_data ON registros_humor(data DESC);
