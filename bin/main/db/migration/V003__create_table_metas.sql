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