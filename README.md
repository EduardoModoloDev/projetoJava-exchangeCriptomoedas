# Exchange de Criptomoedas

Trabalho individual da disciplina CCM310 — Arquitetura de Software e Programação
Orientada a Objetos, da FEI. Aplicação desktop para um investidor movimentar reais,
Bitcoin, Ethereum e Ripple, com interface gráfica e persistência em PostgreSQL.

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

Na compra, o débito em reais inclui a taxa. Na venda, a taxa é descontada do
valor recebido. O saldo e a operação do extrato são gravados na mesma transação.

## Tecnologias

- Java 25, Swing e JDBC.
- PostgreSQL 18 e driver pgJDBC 42.7.14.
- Maven e JUnit 5.13.4.
- Arquitetura MVC.
- Telas criadas no GUI Builder do NetBeans.

O build usa Maven Compiler Plugin 3.14.1, Surefire 3.5.4, JAR Plugin 3.5.1,
Shade Plugin 3.6.2 e Javadoc Plugin 3.6.3.

## Estrutura

- `model` guarda os dados do investidor, da carteira, das moedas e das operações.
- `dao` lê e grava os dados pelo JDBC.
- `controller` executa login, consultas e movimentações, controlando as transações.
- `view` contém os formulários Swing e seus eventos.
- `util` reúne as validações de CPF, senha, ID e valores.

Os pacotes ficam em `src/main/java/br/com/exchange`. Os scripts do banco ficam
em `sql`, e os testes em `src/test/java`.

## Configuração e execução

### 1. Preparar o ambiente

Instale o JDK 25 ou superior, o Maven e o PostgreSQL 18 com pgAdmin 4.
Configure `JAVA_HOME` para o JDK e inclua Java e Maven no `PATH`.
Confira com `java -version` e `mvn -version`.

### 2. Preparar os bancos

1. No pgAdmin, conecte-se ao servidor PostgreSQL.
2. Clique com o botão direito em **Databases > Create > Database** e crie
   `exchange_cripto`.
3. Repita para criar `exchange_cripto_teste`.
4. Selecione `exchange_cripto` e abra **Tools > Query Tool**.
5. Abra e execute `sql/01_schema.sql`. Depois execute `sql/02_dados_iniciais.sql`.
6. Repita os dois scripts no banco `exchange_cripto_teste`.

Os scripts podem ser repetidos e preservam os saldos e as cotações já existentes.
Os testes de integração usam somente `exchange_cripto_teste` e limpam seus dados
antes de cada teste.

### 3. Configurar as conexões locais

Para executar pelo NetBeans, copie `src/main/resources/db.properties.example`
para `src/main/resources/db.properties`. Preencha a URL, o usuário e a sua senha
do PostgreSQL no arquivo local. A URL padrão aponta para
`jdbc:postgresql://localhost:5432/exchange_cripto`.

Para a integração, copie `src/test/resources/db-test.properties.example` para
`src/test/resources/db-test.properties` e preencha os dados locais. Mantenha a URL
apontando para `exchange_cripto_teste`.

Os arquivos `.example` têm valores de exemplo. Os arquivos reais de configuração
são ignorados pelo Git, e a configuração privada é excluída do JAR.

### 4. Compilar

Abra o terminal na raiz do projeto e execute

```sh
mvn clean package
```

O executável é gerado em `target/crypto-exchange-1.0.0.jar`, com o driver JDBC
incluído. O comando também executa os testes. A integração é pulada se a
configuração estiver ausente ou o banco de testes estiver indisponível.

### 5. Executar o JAR

1. Crie uma pasta de execução fora do repositório.
2. Copie `target/crypto-exchange-1.0.0.jar` para essa pasta.
3. Copie `src/main/resources/db.properties.example` para a mesma pasta.
4. Faça uma cópia do exemplo com o nome `db.properties` e preencha os dados
   locais do PostgreSQL.
5. Abra o terminal nessa pasta e execute

```sh
java -jar crypto-exchange-1.0.0.jar
```

A primeira tela é `TelaLogin`. O arquivo `db.properties` é lido da pasta de
trabalho do terminal. Execute o comando na pasta que contém o JAR e esse arquivo.
O PostgreSQL e o banco configurado precisam estar disponíveis no computador
usado para a execução.

### 6. Executar no NetBeans

1. Use **File > Open Project** e selecione a pasta que contém `pom.xml`.
2. Configure a plataforma Java do projeto para JDK 25 ou superior.
3. Abra `br.com.exchange.view.TelaLogin` e escolha **Run File**.

Com o diretório de execução na raiz do projeto, a conexão usa primeiro
`db.properties` dessa pasta. Se ele estiver ausente, a execução pelas classes do
projeto também aceita `src/main/resources/db.properties`.

## Investidores de teste

Dados fictícios cadastrados por `sql/02_dados_iniciais.sql`.

| Nome | CPF | Senha |
|---|---|---|
| Investidor de Teste 1 | 11111111111 | 123456 |
| Investidor de Teste 2 | 22222222222 | 654321 |

## Demonstração

Referências das capturas de tela, a serem adicionadas em `docs/imagens`.

![Login](docs/imagens/01-login.png)
![Menu](docs/imagens/02-menu.png)
![Saldo](docs/imagens/03-saldo.png)
![Extrato](docs/imagens/04-extrato.png)
![Depósito](docs/imagens/05-deposito.png)
![Saque](docs/imagens/06-saque.png)
![Compra](docs/imagens/07-compra.png)
![Venda](docs/imagens/08-venda.png)
![Atualização de cotação](docs/imagens/09-cotacao.png)

<!-- Espaço para o GIF ou vídeo real da demonstração, quando disponível. -->

## Documentação

Gere o Javadoc na raiz do projeto com

```sh
mvn javadoc:javadoc
```

Abra [docs/javadoc/index.html](docs/javadoc/index.html) no navegador.

## Autor

Eduardo Modolo — FEI.
