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