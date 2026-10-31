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

## Banco de dados

O servidor usa PostgreSQL 18 em `localhost:5432`, com o usuário `postgres`.
Os bancos `exchange_cripto` e `exchange_cripto_teste` precisam existir antes
de executar os scripts. Neste ambiente, os dois já foram criados manualmente.

No pgAdmin 4, selecione `exchange_cripto` e abra **Query Tool**. Abra e execute
o arquivo `sql/01_schema.sql` e depois `sql/02_dados_iniciais.sql`. Atualize
**Schemas > public > Tables** para visualizar as quatro tabelas.

Os scripts podem ser repetidos e mantêm os saldos e as cotações já existentes.
Os dois investidores de exemplo são dados fictícios de teste.

Copie `src/main/resources/db.properties.example` para `db.properties` na mesma
pasta. Preencha sua senha somente no arquivo local. Para os testes, copie
`src/test/resources/db-test.properties.example` para `db-test.properties`
na mesma pasta e preencha a senha. Mantenha a URL de teste apontando para
`exchange_cripto_teste`.

Os arquivos com os exemplos são versionados. `db.properties` e
`db-test.properties` ficam fora do Git.

## Testes

Na pasta do projeto, execute `mvn test`.

Os testes de integração preparam as tabelas e limpam os dados de
`exchange_cripto_teste` antes de cada teste. Eles são pulados quando a configuração
não existe, a senha ainda é a de exemplo ou o banco de teste está inacessível.
Uma URL apontando para outro banco é recusada.

Os métodos dos DAOs que recebem uma `Connection` usam a transação de quem chamou.
Isso permite confirmar ou desfazer a atualização dos saldos e do extrato juntos.
