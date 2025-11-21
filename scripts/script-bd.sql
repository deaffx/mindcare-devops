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

-- Tabela de Metas Pessoais
CREATE TABLE metas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descricao VARCHAR(1000),
    categoria VARCHAR(30) NOT NULL,
    tipo VARCHAR(20) NOT NULL DEFAULT 'PRAZO',
    duracao_dias INTEGER CHECK (duracao_dias > 0),
    dias_consecutivos INTEGER DEFAULT 0 CHECK (dias_consecutivos >= 0),
    total_checkpoints INTEGER DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    data_inicio DATE NOT NULL,
    data_fim DATE,
    ultimo_checkin DATE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_meta_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) ON DELETE CASCADE
);
CREATE INDEX idx_meta_usuario_status ON metas(usuario_id, status);
CREATE INDEX idx_meta_status ON metas(status);
CREATE INDEX idx_meta_categoria ON metas(categoria);

-- Tabela de Progresso das Metas
CREATE TABLE progressos_meta (
    id BIGSERIAL PRIMARY KEY,
    meta_id BIGINT NOT NULL,
    data DATE NOT NULL,
    concluido BOOLEAN DEFAULT FALSE,
    observacao VARCHAR(500),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_progresso_meta FOREIGN KEY (meta_id) 
        REFERENCES metas(id) ON DELETE CASCADE,
    CONSTRAINT unique_meta_data UNIQUE (meta_id, data)
);
CREATE INDEX idx_progresso_meta_data ON progressos_meta(meta_id, data DESC);
CREATE INDEX idx_progresso_data ON progressos_meta(data DESC);

-- Tabela de Mensagens da IA
CREATE TABLE mensagens_ia (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    conteudo TEXT NOT NULL,
    contexto TEXT,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_mensagem_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) ON DELETE CASCADE
);
CREATE INDEX idx_mensagem_usuario_lida ON mensagens_ia(usuario_id, lida, criado_em DESC);
CREATE INDEX idx_mensagem_tipo ON mensagens_ia(tipo);
CREATE INDEX idx_mensagem_data ON mensagens_ia(criado_em DESC);
