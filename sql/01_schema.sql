CREATE TABLE IF NOT EXISTS investidor (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf CHAR(11) UNIQUE NOT NULL CHECK (cpf ~ '^[0-9]{11}$'),
    -- Senha em texto apenas neste trabalho. Um sistema real usaria hash.
    senha CHAR(6) NOT NULL CHECK (senha ~ '^[0-9]{6}$')
);

CREATE TABLE IF NOT EXISTS carteira (
    id SERIAL PRIMARY KEY,
    investidor_id INTEGER UNIQUE NOT NULL REFERENCES investidor(id) ON DELETE CASCADE,
    saldo_real NUMERIC(19, 2) NOT NULL DEFAULT 0 CHECK (saldo_real >= 0),
    saldo_bitcoin NUMERIC(28, 8) NOT NULL DEFAULT 0 CHECK (saldo_bitcoin >= 0),
    saldo_ethereum NUMERIC(28, 8) NOT NULL DEFAULT 0 CHECK (saldo_ethereum >= 0),
    saldo_ripple NUMERIC(28, 8) NOT NULL DEFAULT 0 CHECK (saldo_ripple >= 0)
);

CREATE TABLE IF NOT EXISTS operacao (
    id SERIAL PRIMARY KEY,
    investidor_id INTEGER NOT NULL REFERENCES investidor(id),
    data_hora TIMESTAMP NOT NULL,
    sinal CHAR(1) NOT NULL CHECK (sinal IN ('+', '-')),
    valor NUMERIC(19, 2) NOT NULL CHECK (valor >= 0),
    moeda VARCHAR(4) NOT NULL CHECK (moeda IN ('REAL', 'BTC', 'ETH', 'XRP')),
    cotacao NUMERIC(19, 2) NOT NULL CHECK (cotacao >= 0),
    taxa NUMERIC(3, 2) NOT NULL CHECK (taxa >= 0 AND taxa <= 1),
    saldo_real NUMERIC(19, 2) NOT NULL CHECK (saldo_real >= 0),
    saldo_btc NUMERIC(28, 8) NOT NULL CHECK (saldo_btc >= 0),
    saldo_eth NUMERIC(28, 8) NOT NULL CHECK (saldo_eth >= 0),
    saldo_xrp NUMERIC(28, 8) NOT NULL CHECK (saldo_xrp >= 0)
);

CREATE TABLE IF NOT EXISTS cotacao (
    moeda VARCHAR(3) PRIMARY KEY CHECK (moeda IN ('BTC', 'ETH', 'XRP')),
    valor NUMERIC(19, 2) NOT NULL CHECK (valor > 0)
);
