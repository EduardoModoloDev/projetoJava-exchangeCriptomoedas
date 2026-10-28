# Exchange de Criptomoedas

Trabalho individual da disciplina CCM310 — Arquitetura de Software e Programação
Orientada a Objetos.

O projeto está em desenvolvimento. A primeira parte contém as classes do Model
para representar o investidor, a carteira e as moedas.

## Tecnologias

- Java 25
- Maven
- JUnit 5

## Funcionalidades implementadas

- Validação de CPF e senha do investidor.
- Carteira com saldos em reais, Bitcoin, Ethereum e Ripple.
- Adição e remoção de saldo, sem permitir valores negativos.
- Cálculo das taxas de compra e venda.
- Atualização das cotações entre -5% e +5%, mantendo o real fixo.
- Registro das operações no extrato.
- Testes das classes e das regras implementadas.

## Funcionalidades planejadas

- Banco de dados PostgreSQL com JDBC.
- Login e operações de depósito, saque, compra e venda pelos Controllers.
- Telas Swing no NetBeans.
- Executável JAR e documentação final.
