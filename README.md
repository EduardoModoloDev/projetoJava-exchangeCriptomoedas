# Exchange de Criptomoedas

Trabalho individual da disciplina CCM310 — Arquitetura de Software e Programação Orientada a Objetos, da FEI. Aplicação desktop para um investidor movimentar reais, Bitcoin, Ethereum e Ripple, com interface gráfica e dados no PostgreSQL.

## Funcionalidades

- Login com CPF de 11 dígitos, sem pontuação, e senha numérica de 6 dígitos.
- Consulta de saldo e extrato com confirmação de senha.
- Depósito e saque em reais, com verificação de saldo disponível.
- Compra e venda de Bitcoin, Ethereum e Ripple com taxas por moeda.
- Atualização aleatória de cada cotação entre -5% e +5% do valor anterior.
- Extrato com data, valor, moeda, cotação, taxa e saldos após a operação.
- Investidores, saldos, operações e cotações salvos no banco de dados.

| Moeda | Taxa de compra | Taxa de venda |
|---|---|---|
| Bitcoin | 2% | 3% |
| Ethereum | 1% | 2% |
| Ripple | 1% | 1% |

Na compra, o débito em reais inclui a taxa. Na venda, a taxa é descontada do valor recebido. O saldo e a operação do extrato são gravados na mesma transação.

## Tecnologias

- Java, Swing e JDBC.
- PostgreSQL.
- Arquitetura MVC.
- Telas criadas no GUI Builder do NetBeans.

## Estrutura

- `model` guarda os dados do investidor, da carteira, das moedas e das operações.
- `dao` lê e grava os dados pelo JDBC.
- `controller` executa login, consultas e movimentações, controlando as transações.
- `view` contém os formulários Swing e seus eventos.
- `util` reúne as validações de CPF, senha, ID e valores.

Os pacotes ficam em `src/main/java/br/com/exchange`. Os scripts do banco ficam em `sql`, e os testes em `src/test/java`.

## Investidores de teste

Dados fictícios cadastrados por `sql/02_dados_iniciais.sql`.

| Nome | CPF | Senha |
|---|---|---|
| Investidor de Teste 1 | 11111111111 | 123456 |
| Investidor de Teste 2 | 22222222222 | 654321 |

## Autor

Eduardo Modolo — FEI.
