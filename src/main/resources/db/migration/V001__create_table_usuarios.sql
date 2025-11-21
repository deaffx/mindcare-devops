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