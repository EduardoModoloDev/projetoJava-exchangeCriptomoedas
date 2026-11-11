package br.com.exchange.dao;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Abre conexões com o PostgreSQL. */
public final class ConnectionFactory {

    private ConnectionFactory() {
    }

    /**
     * Abre a conexão usando a configuração externa ou a configuração local do NetBeans.
     *
     * @return conexão aberta com o PostgreSQL
     */
    public static Connection getConnection() {
        Properties configuracao = new Properties();
        Path caminho = localizarConfiguracao();
        try (Reader arquivo = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            configuracao.load(arquivo);
        } catch (IOException e) {
            throw new DaoException("Não foi possível ler db.properties.", e);
        }

        String url = configuracao.getProperty("url");
        String usuario = configuracao.getProperty("usuario");
        String senha = configuracao.getProperty("senha");
        if (url == null || url.isBlank() || usuario == null || usuario.isBlank()
                || senha == null || senha.isBlank() || senha.equals("TROQUE_PELA_SUA_SENHA")) {
            throw new DaoException("Preencha url, usuario e senha no arquivo db.properties.");
        }

        Properties acesso = new Properties();
        acesso.setProperty("user", usuario);
        acesso.setProperty("password", senha);
        acesso.setProperty("connectTimeout", "5");
        try {
            return DriverManager.getConnection(url, acesso);
        } catch (SQLException e) {
            throw new DaoException("Confira se o PostgreSQL está rodando e se os dados de db.properties estão corretos.", e);
        }
    }

    private static Path localizarConfiguracao() {
        Path externo = Path.of("db.properties");
        if (Files.isRegularFile(externo)) {
            return externo;
        }

        // No NetBeans, as classes ficam em pastas, fora do JAR.
        URL classe = ConnectionFactory.class.getResource("ConnectionFactory.class");
        if (classe != null && classe.getProtocol().equals("file")) {
            Path local = Path.of("src", "main", "resources", "db.properties");
            if (Files.isRegularFile(local)) {
                return local;
            }
        }
        throw new DaoException("Crie db.properties na pasta de execução, ao lado do JAR, "
                + "copiando db.properties.example. No NetBeans, ele também pode ficar em src/main/resources/db.properties.");
    }
}
