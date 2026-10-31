# Exchange de Criptomoedas

Trabalho individual da disciplina CCM310 — Arquitetura de Software e Programação
Orientada a Objetos.

O projeto está em desenvolvimento. As duas primeiras partes contêm o Model e
o acesso ao banco de dados.

## Tecnologias

- Java 25
- Maven
- PostgreSQL 18 e JDBC (pgJDBC 42.7.14)
- JUnit 5

## Funcionalidades implementadas

- Validação de CPF e senha do investidor.
- Carteira com saldos em reais, Bitcoin, Ethereum e Ripple.
- Adição e remoção de saldo, sem permitir valores negativos.
- Cálculo das taxas de compra e venda.
- Atualização das cotações entre -5% e +5%, mantendo o real fixo.
- Registro das operações no extrato.
- Persistência de investidores, saldos, operações e cotações pelos DAOs.
- Testes do Model, dos DAOs e das transações.

## Funcionalidades planejadas

- Login e operações de depósito, saque, compra e venda pelos Controllers.
- Telas Swing no NetBeans.
- Executável JAR e documentação final.

## Testes

Na pasta do projeto, execute `mvn test`.

Os testes de integração preparam as tabelas e limpam os dados de
`exchange_cripto_teste` antes de cada teste. Eles são pulados quando a configuração
não existe, a senha ainda é a de exemplo ou o banco de teste está inacessível.
Uma URL apontando para outro banco é recusada.

Os métodos dos DAOs que recebem uma `Connection` usam a transação de quem chamou.
Isso permite confirmar ou desfazer a atualização dos saldos e do extrato juntos.
