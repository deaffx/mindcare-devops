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