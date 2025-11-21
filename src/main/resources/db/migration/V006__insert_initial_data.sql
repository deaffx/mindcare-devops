-- Inserir usuário admin de exemplo
INSERT INTO usuarios (nome, email, senha, cargo, role, ativo) 
VALUES (
    'Administrador',
    'admin@mindcare.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- senha: admin123
    'Gestor de Saúde',
    'ROLE_ADMIN',
    true
);

-- Inserir usuário comum de exemplo
INSERT INTO usuarios (nome, email, senha, cargo, role, ativo) 
VALUES (
    'Thiago Moreno',
    'thiago@mindcare.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- senha: admin123
    'Desenvolvedor',
    'ROLE_USER',
    true
);

-- Inserir registros de humor de exemplo para o usuário Thiago
INSERT INTO registros_humor (usuario_id, nivel_humor, emocao, descricao, data)
VALUES 
    (2, 4, 'FELIZ', 'Dia produtivo no trabalho', CURRENT_DATE - INTERVAL '5 days'),
    (2, 5, 'MUITO_FELIZ', 'Finalizei o projeto!', CURRENT_DATE - INTERVAL '4 days'),
    (2, 3, 'NEUTRO', 'Dia tranquilo', CURRENT_DATE - INTERVAL '3 days'),
    (2, 4, 'MOTIVADO', 'Boa reunião com a equipe', CURRENT_DATE - INTERVAL '2 days'),
    (2, 5, 'MUITO_FELIZ', 'Recebi feedback positivo', CURRENT_DATE - INTERVAL '1 day');

-- Inserir metas de exemplo
INSERT INTO metas (usuario_id, titulo, descricao, categoria, tipo, duracao_dias, dias_consecutivos, total_checkpoints, status, data_inicio, data_fim)
VALUES 
    (2, '30 dias de exercícios', 'Fazer 30 minutos de exercício por dia', 'EXERCICIO', 'PRAZO', 30, 5, 5, 'ATIVA', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '25 days'),
    (2, 'Dormir 8h por dia', 'Manter rotina de sono saudável', 'SONO', 'PRAZO', 15, 3, 3, 'ATIVA', CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE + INTERVAL '12 days'),
    (2, 'Alimentação balanceada', 'Comer mais frutas e vegetais', 'ALIMENTACAO', 'PRAZO', 21, 7, 7, 'ATIVA', CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE + INTERVAL '14 days');

-- Inserir progressos das metas
INSERT INTO progressos_meta (meta_id, data, concluido, observacao)
VALUES 
    (1, CURRENT_DATE - INTERVAL '5 days', true, 'Caminhada de 30 minutos'),
    (1, CURRENT_DATE - INTERVAL '4 days', true, 'Corrida leve'),
    (1, CURRENT_DATE - INTERVAL '3 days', true, 'Treino na academia'),
    (1, CURRENT_DATE - INTERVAL '2 days', true, 'Yoga matinal'),
    (1, CURRENT_DATE - INTERVAL '1 day', true, 'Ciclismo'),
    (2, CURRENT_DATE - INTERVAL '3 days', true, 'Dormi 8h30'),
    (2, CURRENT_DATE - INTERVAL '2 days', true, 'Dormi 8h'),
    (2, CURRENT_DATE - INTERVAL '1 day', true, 'Dormi 8h15'),
    (3, CURRENT_DATE - INTERVAL '7 days', true, 'Salada no almoço'),
    (3, CURRENT_DATE - INTERVAL '6 days', true, 'Suco natural'),
    (3, CURRENT_DATE - INTERVAL '5 days', true, 'Frutas no café'),
    (3, CURRENT_DATE - INTERVAL '4 days', true, 'Legumes no jantar'),
    (3, CURRENT_DATE - INTERVAL '3 days', true, 'Smoothie verde'),
    (3, CURRENT_DATE - INTERVAL '2 days', true, 'Salada de frutas'),
    (3, CURRENT_DATE - INTERVAL '1 day', true, 'Vegetais grelhados');

-- Inserir mensagens da IA de exemplo
INSERT INTO mensagens_ia (usuario_id, tipo, conteudo, contexto, lida)
VALUES 
    (2, 'MOTIVACIONAL', 'Você está indo muito bem! Já são 5 dias consecutivos de exercícios 💪', '{"meta_id": 1, "dias_consecutivos": 5}'::jsonb, false),
    (2, 'CONQUISTA', 'Parabéns! Você manteve 100% de consistência na sua alimentação esta semana! 🎉', '{"meta_id": 3, "taxa_sucesso": 100}'::jsonb, false),
    (2, 'LEMBRETE', 'Não se esqueça de registrar seu humor hoje! 🧠', null, true),
    (2, 'MOTIVACIONAL', 'Seu histórico emocional mostra que você está evoluindo. Continue assim! 💚', '{"humor_medio": 4.2}'::jsonb, true);