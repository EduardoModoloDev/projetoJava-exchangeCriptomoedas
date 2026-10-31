INSERT INTO cotacao (moeda, valor) VALUES
    ('BTC', 350000.00),
    ('ETH', 16000.00),
    ('XRP', 2.50)
ON CONFLICT (moeda) DO NOTHING;

-- Dados fictícios para testar a aplicação.
INSERT INTO investidor (nome, cpf, senha) VALUES
    ('Investidor de Teste 1', '11111111111', '123456'),
    ('Investidor de Teste 2', '22222222222', '654321')
ON CONFLICT (cpf) DO NOTHING;

INSERT INTO carteira (investidor_id)
SELECT id FROM investidor WHERE cpf IN ('11111111111', '22222222222')
ON CONFLICT (investidor_id) DO NOTHING;
