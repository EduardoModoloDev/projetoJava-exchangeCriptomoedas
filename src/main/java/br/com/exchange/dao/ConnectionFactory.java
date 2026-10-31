package br.com.exchange.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Abre conexões com o PostgreSQL. */
public final class ConnectionFactory {

    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        Properties configuracao = new Properties();
        try (InputStream arquivo = ConnectionFactory.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (arquivo == null) {
                throw new DaoException("Crie src/main/resources/db.properties a partir do exemplo.");
            }
            configuracao.load(new InputStreamReader(arquivo, StandardCharsets.UTF_8));
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
}
