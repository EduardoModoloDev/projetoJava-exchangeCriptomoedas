package br.com.exchange.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import org.junit.jupiter.api.Assumptions;

/** Configuração e limpeza somente do banco de testes. */
public final class BancoTeste {

    private BancoTeste() {
    }

    static boolean urlDeTeste(String url) {
        if (url == null || !url.startsWith("jdbc:postgresql://")) {
            return false;
        }
        try {
            URI endereco = URI.create(url.substring(5));
            return "/exchange_cripto_teste".equals(endereco.getPath());
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static Connection abrirConexao() {
        Properties configuracao = new Properties();
        try (InputStream arquivo = BancoTeste.class.getClassLoader()
                .getResourceAsStream("db-test.properties")) {
            Assumptions.assumeTrue(arquivo != null, "Crie db-test.properties para executar a integração.");
            configuracao.load(new InputStreamReader(arquivo, StandardCharsets.UTF_8));
        } catch (IOException e) {
            Assumptions.assumeTrue(false, "Não foi possível ler db-test.properties.");
        }

        String url = configuracao.getProperty("url");
        if (!urlDeTeste(url)) {
            throw new IllegalStateException("Os testes aceitam somente o banco exchange_cripto_teste.");
        }
        String usuario = configuracao.getProperty("usuario", "");
        String senha = configuracao.getProperty("senha", "");
        Assumptions.assumeTrue(!usuario.isBlank() && !senha.isBlank()
                && !senha.equals("TROQUE_PELA_SUA_SENHA"), "Preencha a configuração local de testes.");

        Properties acesso = new Properties();
        acesso.setProperty("user", usuario);
        acesso.setProperty("password", senha);
        acesso.setProperty("connectTimeout", "3");
        acesso.setProperty("socketTimeout", "5");
        try {
            Connection conexao = DriverManager.getConnection(url, acesso);
            boolean bancoCorreto = false;
            try {
                bancoCorreto = "exchange_cripto_teste".equals(conexao.getCatalog());
                if (!bancoCorreto) {
                    throw new IllegalStateException("A conexão não está no banco exchange_cripto_teste.");
                }
                return conexao;
            } finally {
                if (!bancoCorreto) {
                    conexao.close();
                }
            }
        } catch (SQLException e) {
            Assumptions.assumeTrue(false, "Banco de teste indisponível. Confira o servidor e db-test.properties.");
            return null;
        }
    }

    public static void preparar(Connection conexao) throws SQLException, IOException {
        // Confere o banco real antes de executar qualquer SQL de limpeza.
        if (!"exchange_cripto_teste".equals(conexao.getCatalog())) {
            throw new IllegalStateException("Limpeza permitida somente em exchange_cripto_teste.");
        }
        executarScript(conexao, "01_schema.sql");
        String sql = "TRUNCATE TABLE operacao, carteira, investidor, cotacao RESTART IDENTITY CASCADE";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.executeUpdate();
        }
        executarScript(conexao, "02_dados_iniciais.sql");
    }

    static void executarScript(Connection conexao, String nome) throws IOException, SQLException {
        String sql = Files.readString(Path.of("sql", nome), StandardCharsets.UTF_8);
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.execute();
        }
    }
}
