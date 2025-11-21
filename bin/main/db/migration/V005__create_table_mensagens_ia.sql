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