# Exchange de Criptomoedas

Trabalho individual da disciplina CCM310 — Arquitetura de Software e Programação
Orientada a Objetos.

O projeto está em desenvolvimento. As três primeiras partes contêm o Model,
o acesso ao banco de dados e os Controllers com as regras de negócio.

## Tecnologias

- Java 25
- Maven
- PostgreSQL 18 e JDBC (pgJDBC 42.7.14)
- JUnit 5

## Funcionalidades implementadas

- Login com CPF e senha e confirmação de senha nas consultas.
- Carteira com saldos em reais, Bitcoin, Ethereum e Ripple.
- Consulta de saldo e extrato atualizados.
- Depósito e saque em reais, sem permitir saldo negativo.
- Compra e venda de Bitcoin, Ethereum e Ripple com as taxas de cada moeda.
- Consulta e atualização das cotações entre -5% e +5%, mantendo o real fixo.
- Extrato com a cotação, a taxa e os saldos após cada operação.
- Persistência de investidores, saldos, operações e cotações pelos DAOs.
- Atualização do saldo e registro no extrato na mesma transação.
- Testes do Model, dos DAOs e dos Controllers.

## Funcionalidades planejadas

- Interface gráfica com Swing no NetBeans, na Parte 4.
- Executável JAR e documentação final.

## Testes

Na pasta do projeto, execute `mvn test`.

Os testes de integração preparam as tabelas e limpam os dados de
`exchange_cripto_teste` antes de cada teste. Eles são pulados quando a configuração
não existe, a senha ainda é a de exemplo ou o banco de teste está inacessível.
Uma URL apontando para outro banco é recusada.
